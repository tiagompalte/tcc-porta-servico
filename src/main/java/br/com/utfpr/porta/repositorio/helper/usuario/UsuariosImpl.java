package br.com.utfpr.porta.repositorio.helper.usuario;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.util.Strings;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Parametro;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.repositorio.Parametros;
import br.com.utfpr.porta.repositorio.filtro.UsuarioFiltro;
import br.com.utfpr.porta.repositorio.paginacao.PaginacaoUtil;

public class UsuariosImpl implements UsuariosQueries {
	
	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@Autowired
	private Parametros parametroRepositorio;
	
	public Optional<Usuario> porEmailEAtivo(String email) {
		return manager
				.createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
				.setParameter("email", email).getResultList().stream().findFirst();
	}
	
	public List<String> permissoes(Usuario usuario) {
		return manager
				.createQuery("select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
				.setParameter("usuario", usuario)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<Usuario> filtrar(UsuarioFiltro filtro, Pageable pageable) {		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("pessoa", "p");
		criteria.addOrder(Order.asc("p.nome"));
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);		
		List<Usuario> filtrados = criteria.list();
		return new PageImpl(filtrados, pageable, total(filtro));
	}
		
	private Long total(UsuarioFiltro filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("pessoa", "p");
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(UsuarioFiltro filtro, Criteria criteria) {
		if (filtro != null) {
			if (!Strings.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("p.nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if (!Strings.isEmpty(filtro.getEmail())) {
				criteria.add(Restrictions.ilike("email", filtro.getEmail(), MatchMode.START));
			}					
		}
		
		Parametro codGrpUsuario = parametroRepositorio.findOne("COD_GRP_USUARIO");
		if(codGrpUsuario == null || Strings.isEmpty(codGrpUsuario.getValor())) {
			throw new NullPointerException("COD_GRP_USUARIO não foi parametrizado");
		}
		
		criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("g.codigo", codGrpUsuario.getValorLong()));
	}

	@Transactional(readOnly = true)
	public Usuario buscarComGrupos(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (Usuario) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked") 
	@Transactional(readOnly = true)
	public List<Usuario> buscarPorGrupoCodigoAndAtivo(Long grupoCodigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("g.codigo", grupoCodigo));
		criteria.add(Restrictions.eq("ativo", true));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}
	
	@Transactional
	public int apagarNomeAudio(String nomeAudio) {
		
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaUpdate<Usuario> criteria = builder.createCriteriaUpdate(Usuario.class);
		Root<Usuario> root = criteria.from(Usuario.class);

		criteria.set("nomeAudio", null)
			.where(builder.equal(root.<String>get("nomeAudio"), nomeAudio));
		
		return this.manager.createQuery(criteria).executeUpdate();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Optional<Usuario> porEmailEAtivoComGrupos(String email) {		
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("email", email));
		criteria.add(Restrictions.eq("ativo", Boolean.TRUE));		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria.list().stream().findFirst();
	}

	@Transactional
	public int gravarNomeAudio(Long codigoUsuario, String nomeAudio) {
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaUpdate<Usuario> criteria = builder.createCriteriaUpdate(Usuario.class);
		Root<Usuario> root = criteria.from(Usuario.class);

		criteria.set("nomeAudio", nomeAudio)
			.where(builder.equal(root.<Long>get("codigo"), codigoUsuario));
		
		return this.manager.createQuery(criteria).executeUpdate();
	}
		
}
