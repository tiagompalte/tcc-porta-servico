package br.com.utfpr.porta.repositorio.helper.anuncio_usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.util.Strings;
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
import br.com.utfpr.porta.modelo.AnuncioUsuario;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.filtro.AnuncioUsuarioFiltro;
import br.com.utfpr.porta.repositorio.paginacao.PaginacaoUtil;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

public class AnuncioUsuarioImpl implements AnuncioUsuarioQueries {
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@Transactional
	public int excluirPorAnuncio(Anuncio anuncio) {
		
		if(anuncio == null || anuncio.getCodigo() == null) {
			throw new NullPointerException("Anúncio não informado");
		}

		try {			
			CriteriaBuilder builder = this.manager.getCriteriaBuilder();
			CriteriaDelete<AnuncioUsuario> criteria = builder.createCriteriaDelete(AnuncioUsuario.class);
			Root<AnuncioUsuario> root = criteria.from(AnuncioUsuario.class);
			
			criteria.where(builder.equal(root.<Anuncio>get("id.anuncio"), anuncio));
			
			return this.manager.createQuery(criteria).executeUpdate();
		}
		catch(Exception e) {
			throw new ValidacaoBancoDadosExcecao("Erro ao excluir relação de usuários interessados pelo anúncio");
		}
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public List<Usuario> obterListaUsuariosPorAnuncio(Long codigo_anuncio) {
		
		if(codigo_anuncio == null) {
			return null;
		}
				
		List<Long> listaCodigosUsr = manager
				.createQuery("select id.usuario.codigo from AnuncioUsuario where id.anuncio.codigo = ?", Long.class)
				.setParameter(1, codigo_anuncio)
				.getResultList();
		
		if(listaCodigosUsr == null || listaCodigosUsr.isEmpty()) {
			return null;
		}
		
		Criteria criteriaUsr = manager.unwrap(Session.class).createCriteria(Usuario.class);		
		criteriaUsr.add(Restrictions.in("codigo", listaCodigosUsr));		
		return criteriaUsr.list();
	}
			
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<Anuncio> filtrar(AnuncioUsuarioFiltro filtro, Pageable pageable) {	
		
		if(Strings.isEmpty(filtro.getCidade()) && Strings.isEmpty(filtro.getEstado()) 
				&& filtro.getFaixaPrecoInicial() == null && filtro.getFaixaPrecoFinal() == null) {
			return new PageImpl<Anuncio>(new ArrayList<Anuncio>(), pageable, 0);
		}
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Anuncio.class);
		criteria.createAlias("estabelecimento", "est");
		criteria.createAlias("est.endereco", "end");
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		List<Anuncio> filtrados = criteria.list();
				
		return new PageImpl<Anuncio>(filtrados, pageable, total(filtro));
	}
	
	private Long total(AnuncioUsuarioFiltro filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Anuncio.class);
		criteria.createAlias("estabelecimento", "est");
		criteria.createAlias("est.endereco", "end");
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(AnuncioUsuarioFiltro filtro, Criteria criteria) {				
		
		if (filtro != null) {
			
			//NÃO BUSCAR POR ANÚNCIOS EXPIRADOS
			criteria.add(Restrictions.ge("dataExpiracao", LocalDate.now()));
			
			if(filtro.getFaixaPrecoInicial() != null && filtro.getFaixaPrecoFinal() != null) {
				criteria.add(Restrictions.between("preco", filtro.getFaixaPrecoInicial(), filtro.getFaixaPrecoFinal()));
			}
			else if(filtro.getFaixaPrecoInicial() != null) {
				criteria.add(Restrictions.ge("preco", filtro.getFaixaPrecoInicial()));
			}
			else if(filtro.getFaixaPrecoFinal() != null) {
				criteria.add(Restrictions.le("preco", filtro.getFaixaPrecoFinal()));
			}
			
			if(Strings.isNotEmpty(filtro.getCidade())) {
				criteria.add(Restrictions.eq("end.cidade", filtro.getCidade()));
			}
			
			if(Strings.isNotEmpty(filtro.getEstado())) {
				criteria.add(Restrictions.eq("end.estado", filtro.getEstado()));
			}
			
		}
	}
	
}
