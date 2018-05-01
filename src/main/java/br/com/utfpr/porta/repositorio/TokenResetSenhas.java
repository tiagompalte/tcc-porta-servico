package br.com.utfpr.porta.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.utfpr.porta.modelo.TokenResetSenha;
import br.com.utfpr.porta.modelo.TokenResetSenhaId;
import br.com.utfpr.porta.repositorio.helper.token_reset_senhas.TokenResetSenhasQueries;

@Repository
public interface TokenResetSenhas extends JpaRepository<TokenResetSenha, TokenResetSenhaId>, TokenResetSenhasQueries {
	
	public Optional<TokenResetSenha> findByToken(String token);
		
}
