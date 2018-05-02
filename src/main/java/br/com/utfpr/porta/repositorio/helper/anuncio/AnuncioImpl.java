package br.com.utfpr.porta.repositorio.helper.anuncio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.repositorio.filtro.AnuncioFiltro;
import br.com.utfpr.porta.repositorio.paginacao.PaginacaoUtil;

public class AnuncioImpl implements AnuncioQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
		
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<Anuncio> filtrar(AnuncioFiltro filtro, Pageable pageable) {	
		
		if(filtro.getEstabelecimento() == null || filtro.getEstabelecimento().getCodigo() == null) {
			return new PageImpl<Anuncio>(new ArrayList<Anuncio>(), pageable, 0);
		}
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Anuncio.class);		
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		List<Anuncio> filtrados = criteria.list();
		
		if(filtrados != null && filtrados.isEmpty() == false) {
			for(Anuncio anuncio : filtrados) {
				anuncio.setQtdeInteressados(obterQtdeUsuariosInteressadosPorAnuncio(anuncio.getCodigo()));
			}
		}
				
		return new PageImpl<Anuncio>(filtrados, pageable, total(filtro));
	}
	
	private Long total(AnuncioFiltro filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Anuncio.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(AnuncioFiltro filtro, Criteria criteria) {				
		if (filtro != null) {			
			if(filtro.getEstabelecimento() != null) {
				criteria.add(Restrictions.eq("estabelecimento", filtro.getEstabelecimento()));
			}
			
			if(filtro.getDataInicio() != null && filtro.getDataFinal() != null) {
				criteria.add(Restrictions.between("dataPublicacao", filtro.getDataInicio(), filtro.getDataFinal()));
			}
			else if(filtro.getDataInicio() != null) {
				criteria.add(Restrictions.ge("dataPublicacao", filtro.getDataInicio()));
			}
			else if(filtro.getDataFinal() != null) {
				criteria.add(Restrictions.le("dataPublicacao", filtro.getDataFinal()));
			}
			
			if(filtro.isExpirado() == false) {
				criteria.add(Restrictions.ge("dataExpiracao", LocalDate.now()));
			}
		}
	}
	
	private Long obterQtdeUsuariosInteressadosPorAnuncio(Long codigo_anuncio) {
		
		if(codigo_anuncio == null) {
			throw new NullPointerException("Código do anúncio não informado");
		}
		
		return manager
				.createQuery("select count(*) from AnuncioUsuario where codigo_anuncio = :codigo", Long.class)
				.setParameter("codigo", codigo_anuncio)
				.getSingleResult();		
	}

}
