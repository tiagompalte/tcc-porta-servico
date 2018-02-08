package br.com.utfpr.porta.repositorio.helper.autorizacao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

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

import br.com.utfpr.porta.modelo.Autorizacao;
import br.com.utfpr.porta.modelo.TipoAutorizacao;
import br.com.utfpr.porta.repositorio.filtro.AutorizacaoFiltro;
import br.com.utfpr.porta.repositorio.paginacao.PaginacaoUtil;

public class AutorizacoesImpl implements AutorizacoesQueries {
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Autorizacao> findByCodigoUsuarioAndCodigoPorta(Long codigoUsuario, Long codigoPorta) {		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Autorizacao.class);
		criteria.add(Restrictions.eq("id.usuario.codigo", codigoUsuario));
		criteria.add(Restrictions.eq("id.porta.codigo", codigoPorta));		
		criteria.addOrder(Order.asc("tipoAutorizacao"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Autorizacao> findByCodigoPorta(Long codigoPorta) {		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Autorizacao.class);		
		criteria.add(Restrictions.eq("id.porta.codigo", codigoPorta));		
		criteria.addOrder(Order.asc("tipoAutorizacao"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Autorizacao> findByCodigoUsuario(Long codigoUsuario) {		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Autorizacao.class);		
		criteria.add(Restrictions.eq("id.usuario.codigo", codigoUsuario));		
		criteria.addOrder(Order.asc("tipoAutorizacao"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<Autorizacao> filtrar(AutorizacaoFiltro filtro, Pageable pageable) {		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Autorizacao.class);		
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);		
		List<Autorizacao> filtrados = criteria.list();
		return new PageImpl<Autorizacao>(filtrados, pageable, total(filtro));
	}
	
	private Long total(AutorizacaoFiltro filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Autorizacao.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(AutorizacaoFiltro filtro, Criteria criteria) {
		if (filtro != null) {
			if (filtro.getUsuario() != null && filtro.getUsuario().getCodigo() != null) {
				criteria.add(Restrictions.eq("id.usuario", filtro.getUsuario()));
			}
			if (filtro.getPorta() != null && filtro.getPorta().getCodigo() != null) {
				criteria.add(Restrictions.eq("id.porta", filtro.getPorta()));
			}
			if (filtro.getTipoAutorizacao() != null && filtro.getTipoAutorizacao() != null) {
				criteria.add(Restrictions.eq("tipoAutorizacao", filtro.getTipoAutorizacao()));
			}
			if (filtro.getEstabelecimento() != null && filtro.getEstabelecimento().getCodigo() != null) {
				criteria.add(Restrictions.eq("id.estabelecimento", filtro.getEstabelecimento()));
			}
		}
	}
	
	@Transactional()
	public void apagarAutorizacoesTemporariasVencidas(Date dataHoraAtual) {
		
		LocalDateTime localDateTimeAtual;
		if(dataHoraAtual == null) {
			localDateTimeAtual = LocalDateTime.now().minusDays(1);
		}
		else {
			localDateTimeAtual = LocalDateTime.ofInstant(dataHoraAtual.toInstant(), 
									ZoneId.from(dataHoraAtual.toInstant())).minusDays(1);
		}
						
		CriteriaBuilder cb = manager.getCriteriaBuilder();
		CriteriaDelete<Autorizacao> delete = cb.createCriteriaDelete(Autorizacao.class);
		Root<Autorizacao> autorizacao = delete.getRoot();
		delete.where(cb.and(
				cb.equal(autorizacao.get("tipoAutorizacao"), TipoAutorizacao.TEMPORARIO.name()),
				cb.lessThan(autorizacao.get("dataHoraFim"), localDateTimeAtual)));
		manager.createQuery(delete).executeUpdate();	
	}
	
}
