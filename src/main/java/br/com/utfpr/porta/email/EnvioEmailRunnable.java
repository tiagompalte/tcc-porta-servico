package br.com.utfpr.porta.email;

public class EnvioEmailRunnable implements Runnable {
	
	private String email;	
	private EmailServico emailServico;	
	
	public EnvioEmailRunnable(String email, EmailServico emailServico) {
		this.email = email;
		this.emailServico = emailServico;
	}

	@Override
	public void run() {
		emailServico.enviarEmailResetSenha(email);
	}

}
