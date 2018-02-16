package br.com.utfpr.porta.repositorio.helper.anuncio_usuario;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.Anuncio;
import br.com.utfpr.porta.modelo.AnuncioUsuario;
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
	
}
