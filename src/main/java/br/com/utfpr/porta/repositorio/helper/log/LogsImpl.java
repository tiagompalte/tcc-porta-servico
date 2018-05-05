package br.com.utfpr.porta.repositorio.helper.log;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Estabelecimento;
import br.com.utfpr.porta.modelo.Log;
import br.com.utfpr.porta.repositorio.paginacao.PaginacaoUtil;

public class LogsImpl implements LogsQueries {
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;	

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<Log> filtrar(Estabelecimento estabelecimento, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFinal, Pageable pageable) {
		
		if(dataHoraInicio == null || dataHoraFinal == null) {
			return new PageImpl(new ArrayList<Log>(), pageable, 0);
		}
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Log.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(estabelecimento, dataHoraInicio, dataHoraFinal, criteria);		
		List<Log> filtrados = criteria.list();
		return new PageImpl(filtrados, pageable, total(estabelecimento, dataHoraInicio, dataHoraFinal));
	}
	
	private Long total(Estabelecimento estabelecimento, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFinal) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Log.class);
		adicionarFiltro(estabelecimento, dataHoraInicio, dataHoraFinal, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(Estabelecimento estabelecimento, LocalDateTime dataHoraInicio, 
			LocalDateTime dataHoraFinal, Criteria criteria) {
		criteria.add(Restrictions.between("dataHora", dataHoraInicio, dataHoraFinal));	
		if(estabelecimento != null && estabelecimento.getCodigo() != null) {			
			criteria.add(Restrictions.eq("estabelecimento", estabelecimento));	
		}
	}

	@Override
	@Transactional
	public void apagarLogsAteData(LocalDateTime dataAte) {
				
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaDelete<Log> criteria = builder.createCriteriaDelete(Log.class);
		Root<Log> root = criteria.from(Log.class);

		criteria.where(builder.and(
				builder.lessThan(root.<LocalDateTime>get("dataHora"), dataAte)));
		
		this.manager.createQuery(criteria).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Log> filtrarSemPage(Estabelecimento estabelecimento, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFinal) {
		
		if(dataHoraInicio == null || dataHoraFinal == null) {
			return new ArrayList<>();
		}
		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Log.class);
		adicionarFiltro(estabelecimento, dataHoraInicio, dataHoraFinal, criteria);		
		return criteria.list();
	}
	
}
