package br.com.utfpr.porta.repositorio.helper.endereco;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.util.Strings;
import org.springframework.transaction.annotation.Transactional;

public class EnderecoImpl implements EnderecoQueries {
	
	@PersistenceContext
	private EntityManager manager;

	@Transactional(readOnly=true)
	public List<String> obterEstados() {		
		return manager.createQuery("SELECT estado FROM Endereco GROUP BY estado", String.class).getResultList();
	}

	@Override
	public List<String> obterCidadesPorEstado(String estado) {
		
		if(Strings.isEmpty(estado)) {
			throw new NullPointerException("Estado não informado");
		}
		
		return manager.createQuery("SELECT cidade FROM Endereco GROUP BY cidade WHERE estado = ?", String.class)
				.setParameter(1, estado.toUpperCase()).getResultList();
	}

}
