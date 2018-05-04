package br.com.utfpr.porta.repositorio.helper.estabelecimento;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.util.Strings;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Estabelecimento;
import br.com.utfpr.porta.repositorio.filtro.EstabelecimentoFiltro;
import br.com.utfpr.porta.repositorio.paginacao.PaginacaoUtil;

public class EstabelecimentosImpl implements EstabelecimentosQueries {
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<Estabelecimento> filtrar(EstabelecimentoFiltro filtro, Pageable pageable) {		
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Estabelecimento.class);
		criteria.createAlias("endereco", "end");
		criteria.addOrder(Order.asc("codigo"));
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		List<Estabelecimento> filtrados = criteria.list();
		
		if(filtrados != null && !filtrados.isEmpty()) {
			for(Estabelecimento est : filtrados) {
				est.setQuantidadePortas(quantidadePortasPorEstabelecimento(est.getCodigo()));
				est.setQuantidadeAnuncios(quantidadeAnunciosPorEstabelecimento(est.getCodigo()));
			}
		}
				
		return new PageImpl(filtrados, pageable, total(filtro));
	}
	
	private Long total(EstabelecimentoFiltro filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Estabelecimento.class);
		criteria.createAlias("endereco", "end");
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(EstabelecimentoFiltro filtro, Criteria criteria) {
		if (filtro != null) {
			if (Strings.isNotEmpty(filtro.getCidade())) {
				criteria.add(Restrictions.eq("end.cidade", filtro.getCidade()));
			}
			if (Strings.isNotEmpty(filtro.getEstado())) {
				criteria.add(Restrictions.eq("end.estado", filtro.getEstado()));
			}
		}
	}
		
	private Long quantidadePortasPorEstabelecimento(Long codigoEstabelecimento) {
		
		if(codigoEstabelecimento == null) {
			throw new NullPointerException("Código do estabelecimento não informado");
		}
		
		return manager
				.createQuery("select count(*) from Porta where codigo_estabelecimento = :codigo", Long.class)
				.setParameter("codigo", codigoEstabelecimento)
				.getSingleResult();
	}
	
	private Long quantidadeAnunciosPorEstabelecimento(Long codigoEstabelecimento) {
		
		if(codigoEstabelecimento == null) {
			throw new NullPointerException("Código do estabelecimento não informado");
		}
		
		return manager
				.createQuery("select count(*) from Anuncio where codigo_estabelecimento = :codigo", Long.class)
				.setParameter("codigo", codigoEstabelecimento)
				.getSingleResult();
	}

}
