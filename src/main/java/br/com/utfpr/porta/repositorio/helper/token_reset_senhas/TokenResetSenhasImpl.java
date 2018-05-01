package br.com.utfpr.porta.repositorio.helper.token_reset_senhas;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.springframework.transaction.annotation.Transactional;

import br.com.utfpr.porta.modelo.TokenResetSenha;

public class TokenResetSenhasImpl implements TokenResetSenhasQueries {
	
	@PersistenceContext
	private EntityManager manager;
	
	@Transactional
	public void apagarTokensVencidos(Date dataHoraAtual) {
		
		LocalDateTime localDateTimeExpiraTokens;
		if(dataHoraAtual == null) {
			localDateTimeExpiraTokens = LocalDateTime.now().minusDays(3);
		}
		else {
			localDateTimeExpiraTokens = LocalDateTime.ofInstant(dataHoraAtual.toInstant(), ZoneId.systemDefault()).minusDays(3);			
		}
		
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaDelete<TokenResetSenha> criteria = builder.createCriteriaDelete(TokenResetSenha.class);
		Root<TokenResetSenha> root = criteria.from(TokenResetSenha.class);

		criteria.where(builder.lessThan(root.<LocalDateTime>get("dataHoraCriacao"), localDateTimeExpiraTokens));
		
		this.manager.createQuery(criteria).executeUpdate();
	}

}
