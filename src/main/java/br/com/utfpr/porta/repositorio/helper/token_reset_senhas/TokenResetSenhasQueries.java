package br.com.utfpr.porta.repositorio.helper.token_reset_senhas;

import java.util.Date;

public interface TokenResetSenhasQueries {
	
	public void apagarTokensVencidos(Date dataHoraAtual);

}
