package br.com.utfpr.porta.servico;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Endereco;
import br.com.utfpr.porta.modelo.Estabelecimento;
import br.com.utfpr.porta.modelo.Log;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.Enderecos;
import br.com.utfpr.porta.repositorio.Estabelecimentos;
import br.com.utfpr.porta.repositorio.Logs;
import br.com.utfpr.porta.repositorio.Usuarios;
import br.com.utfpr.porta.servico.excecao.EnderecoJaCadastradoExcecao;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

@Service
public class EstabelecimentoServico {
	
	@Autowired
	private Estabelecimentos estabelecimentosRepositorio;
	
	@Autowired
	private Enderecos enderecosRespositorio;
	
	@Autowired
	private UsuarioServico usuarioServico;
	
	@Autowired
	private Usuarios usuarioRepositorio;
	
	@Autowired
	private Logs logRepositorio;
	
	@Transactional
	public void salvar(Estabelecimento estabelecimento) {
		
		if(estabelecimento == null) {
			throw new NullPointerException("Estabelecimento não informado");
		}
		
		if(estabelecimento.getEndereco() == null) {
			throw new NullPointerException("Endereço não informado");
		}
		
		boolean estabelecimento_novo = false;
		if(estabelecimento.isNovo()) {
			estabelecimento_novo = true;
			Endereco enderecoBase = enderecosRespositorio.findByCepAndNumero(
					estabelecimento.getEndereco().getCep(), estabelecimento.getEndereco().getNumero());
			
			if(enderecoBase != null && 
					((estabelecimento.getEndereco().getComplemento() != null && enderecoBase.getComplemento() != null 
						&& estabelecimento.getEndereco().getComplemento().trim().compareTo(enderecoBase.getComplemento().trim()) == 0)
					|| (estabelecimento.getEndereco().getComplemento() == null && enderecoBase.getComplemento() == null))) {
				throw new EnderecoJaCadastradoExcecao("Endereço já cadastrado");
			}
		}
				
		if(estabelecimento.getResponsavel() == null) {
			throw new NullPointerException("Responsável não informado");
		}
				
		Endereco enderecoSalvo = enderecosRespositorio.save(estabelecimento.getEndereco());
		
		if(enderecoSalvo == null || enderecoSalvo.getCodigo() == null) {
			throw new ValidacaoBancoDadosExcecao("Não foi possível salvar o endereço do estabelecimento"); 
		}
		
		estabelecimento.setEndereco(enderecoSalvo);
		
		Usuario usuarioSalvo = usuarioServico.salvar(estabelecimento.getResponsavel());
		
		if(usuarioSalvo == null || usuarioSalvo.getCodigo() == null) {
			throw new ValidacaoBancoDadosExcecao("Não foi possível salvar o responsável do estabelecimento"); 
		}
		
		if(estabelecimento.getResponsavel().isNovo()) {
			estabelecimento.setResponsavel(usuarioSalvo);
		}
		
		if(estabelecimento.isNovo()) {			
			estabelecimento.setAtivo(Boolean.TRUE);
		}
							
		Estabelecimento estabelecimentoSalvo = estabelecimentosRepositorio.save(estabelecimento);
		
		if(estabelecimentoSalvo != null && estabelecimentoSalvo.getCodigo() != null && estabelecimento_novo) {
			usuarioSalvo.setEstabelecimento(estabelecimentoSalvo);
			usuarioRepositorio.save(usuarioSalvo);
		}
				
	}
	
	@Transactional(rollbackFor={ImpossivelExcluirEntidadeException.class,NullPointerException.class,Exception.class})
	public void excluir(Long codigo) {
		
		if(codigo == null) {
			throw new NullPointerException("Código do estabelecimento não informado");
		}
		
		Estabelecimento estabelecimento = estabelecimentosRepositorio.findOne(codigo);
		
		if(estabelecimento == null) {
			throw new ImpossivelExcluirEntidadeException("Estabelecimento não encontrado na base de dados");
		}
		
		//Excluir endereço
		if(estabelecimento.getEndereco() != null) {			
			try {
				enderecosRespositorio.delete(estabelecimento.getEndereco());
				enderecosRespositorio.flush();
			}
			catch(PersistenceException e) {
				throw new ImpossivelExcluirEntidadeException("Erro ao excluir endereço do estabelecimento");
			}
		}
		
		//Excluir responsável
		if(estabelecimento.getResponsavel() != null) {
			try {
				usuarioRepositorio.delete(estabelecimento.getResponsavel());
				usuarioRepositorio.flush();
			}
			catch(PersistenceException e) {
				throw new ImpossivelExcluirEntidadeException("Erro ao excluir responsável do estabelecimento");
			}
		}
		
		//Excluir os logs
		List<Log> listaLogs = logRepositorio.findByEstabelecimento(estabelecimento);
		if(listaLogs != null && listaLogs.isEmpty() == false) {			
			try {
				logRepositorio.deleteInBatch(listaLogs);
				logRepositorio.flush();
			}
			catch(PersistenceException e) {
				throw new ImpossivelExcluirEntidadeException("Erro ao excluir logs do estabelecimento");
			}
		}
		
		try {
			estabelecimentosRepositorio.delete(codigo);
			estabelecimentosRepositorio.flush();
		}
		catch(PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível excluir estabelecimento. Ele está vinculado a portas e autorizações");
		}
		
	}

}
