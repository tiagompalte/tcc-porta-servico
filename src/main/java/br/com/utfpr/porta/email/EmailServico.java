package br.com.utfpr.porta.email;

public interface EmailServico {
	
	public void enviarEmail(String destinatario, String titulo, String mensagem);
	
}
