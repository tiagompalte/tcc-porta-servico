package br.com.utfpr.porta.servico;

import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Autorizacao;
import br.com.utfpr.porta.modelo.Estabelecimento;
import br.com.utfpr.porta.modelo.Parametro;
import br.com.utfpr.porta.modelo.Porta;
import br.com.utfpr.porta.repositorio.Autorizacoes;
import br.com.utfpr.porta.repositorio.Parametros;
import br.com.utfpr.porta.repositorio.Portas;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

@Service
public class PortaServico {
		
	@Autowired
	private Portas portasRepositorio;
	
	@Autowired
	private Autorizacoes autorizacoesRepositorio;
	
	@Autowired
	private Parametros parametrosRepositorio;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private boolean portaPossuiAutorizacoes(Porta porta) {
		
		List<Autorizacao> listaAutorizacao = autorizacoesRepositorio.findByCodigoPorta(porta.getCodigo());
		
		return listaAutorizacao != null && !listaAutorizacao.isEmpty();		
	}
			
	@Transactional
	public void salvar(Porta porta) {
		
		if(porta == null) {
			throw new NullPointerException("Porta não informada");
		}
		
		if(porta.isNovo() || porta.getEstabelecimento() == null) {
			
			Parametro parCodEstSistema = parametrosRepositorio.findOne("COD_EST_SISTEMA");
			
			if(parCodEstSistema == null || Strings.isEmpty(parCodEstSistema.getValor())) {
				throw new NullPointerException("COD_EST_SISTEMA não parametrizado");
			}
			
			porta.setEstabelecimento(new Estabelecimento(parCodEstSistema.getValorLong()));
		}
		
		if(!porta.isNovo()) {
			
			Porta portaBase = portasRepositorio.findOne(porta.getCodigo());
			
			if(portaBase == null) {
				throw new ValidacaoBancoDadosExcecao("Não foi possível encontrar essa porta na base de dados");
			}
			
			//Só é possível alterar estabelecimento da porta se ela não tiver nenhuma autorização vinculada
			if(portaBase.getEstabelecimento() != null && porta.getEstabelecimento() != null 
					&& !porta.getEstabelecimento().equals(portaBase.getEstabelecimento())
					&& portaPossuiAutorizacoes(porta)) {
				throw new ImpossivelExcluirEntidadeException("Impossível excluir porta. Ele possui autorizações vinculadas");				
			}	
			
			//Só é possível alterar porta, informando a senha da porta
			if(!(passwordEncoder.matches(porta.getSenha(), portaBase.getSenha()) || porta.getSenha().equals(portaBase.getSenha()))) {					
				throw new ValidacaoBancoDadosExcecao("Senha não confere");
			}
			
		}
		else {
			porta.setSenha(this.passwordEncoder.encode(porta.getSenha()));
		}
						
		portasRepositorio.save(porta);
	}
		
	@Transactional
	public void excluir(Long codigo) {
		
		if(codigo == null) {
			throw new NullPointerException("Código da porta não informado");
		}
		
		try {
			portasRepositorio.delete(codigo);
			portasRepositorio.flush();
		}
		catch(PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível excluir porta. Ele possui autorizações vinculadas");
		}
		
	}
	
}
