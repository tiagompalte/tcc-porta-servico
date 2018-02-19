package br.com.utfpr.porta.repositorio.helper.anuncio_usuario;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.modelo.AnuncioUsuario;
import br.com.utfpr.porta.modelo.Usuario;
import br.com.utfpr.porta.servico.excecao.ValidacaoBancoDadosExcecao;

public class AnuncioUsuarioImpl implements AnuncioUsuarioQueries {
	
	@PersistenceContext
	private EntityManager manager;

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
	
}
