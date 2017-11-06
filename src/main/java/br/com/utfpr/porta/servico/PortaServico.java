package br.com.utfpr.porta.servico;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Autorizacao;
import br.com.utfpr.porta.modelo.Estabelecimento;
import br.com.utfpr.porta.modelo.Porta;
import br.com.utfpr.porta.repositorio.Autorizacoes;
import br.com.utfpr.porta.repositorio.Portas;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;

@Service
public class PortaServico {
	
	@Autowired
	private Portas portasRepositorio;
	
	@Autowired
	private Autorizacoes autorizacoesRepositorio;
			
	@Transactional
	public void salvar(Porta porta) {
		
		if(porta == null) {
			throw new NullPointerException("Porta não informada");
		}
						
		portasRepositorio.save(porta);
	}
	
	@Transactional
	public void modificarEstabelecimento(Porta porta, Estabelecimento estabelecimento) {
		
		if(porta == null) {
			throw new NullPointerException("Porta não informada");
		}
		
		if(estabelecimento == null) {
			throw new NullPointerException("Estabelecimento não informado");
		}
		
		porta.setEstabelecimento(estabelecimento);
		
		List<Autorizacao> listaAutorizacao = autorizacoesRepositorio.findByCodigoPorta(porta.getCodigo());
		
		if(listaAutorizacao != null && listaAutorizacao.isEmpty() == false) {
			throw new ImpossivelExcluirEntidadeException("Impossível excluir porta. Ele possui autorizações vinculadas.");
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
			throw new ImpossivelExcluirEntidadeException("Impossível excluir porta. Ele possui autorizações vinculadas.");
		}
		
	}
	
}
