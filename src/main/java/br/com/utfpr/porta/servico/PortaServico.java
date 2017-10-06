package br.com.utfpr.porta.servico;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Porta;
import br.com.utfpr.porta.repositorio.Portas;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;

@Service
public class PortaServico {
	
	@Autowired
	private Portas portasRepositorio;
			
	@Transactional
	public void salvar(Porta porta) {
		
		if(porta == null) {
			throw new NullPointerException("Porta não informada");
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
