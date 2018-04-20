package br.com.utfpr.porta.email;

public interface EmailServico {
	
	public void envioEmail(String remetente, String destinatario, String titulo, String mensagem);
	
}
