package br.com.utfpr.porta.servico;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.utfpr.porta.modelo.Parametro;
import br.com.utfpr.porta.repositorio.Parametros;
import br.com.utfpr.porta.servico.excecao.ImpossivelExcluirEntidadeException;

@Service
public class ParametroServico {
	
	@Autowired
	private Parametros parametroRepositorio;
	
	@Transactional
	public void salvar(Parametro parametro) {
		
		if(parametro == null) {
			throw new NullPointerException("Entidade parâmetro está nulo");
		}
		
		if(!StringUtils.isEmpty(parametro.getCodigo())) {
			parametro.setCodigo(parametro.getCodigo().toUpperCase());
		}
		
		parametroRepositorio.save(parametro);
		
	}
	
	@Transactional
	public void excluir(String codigo) {
		
		if(codigo == null) {
			throw new NullPointerException("Código do parâmetro não informado");
		}
		
		try {
			parametroRepositorio.delete(codigo);
			parametroRepositorio.flush();
		}
		catch(PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Não foi possível apagar o parâmetro");
		}
		
	}
	
}
