package br.com.utfpr.porta.email;

public class EnvioEmailRunnable implements Runnable {
	
	private String destinatario;
	private String titulo;
	private String mensagem;
	private EmailServico emailServico;	
	
	public EnvioEmailRunnable(String destinatario, String titulo, String mensagem, EmailServico emailServico) {
		this.destinatario = destinatario;
		this.titulo = titulo;
		this.mensagem = mensagem;
		this.emailServico = emailServico;
	}

	@Override
	public void run() {
		emailServico.enviarEmail(destinatario, titulo, mensagem);
	}

}
