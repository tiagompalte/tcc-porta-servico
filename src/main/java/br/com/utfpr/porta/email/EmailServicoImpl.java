package br.com.utfpr.porta.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class EmailServicoImpl implements EmailServico {
	
	@Autowired
	private MailSender mailSender;
	
	@Override
	public void envioEmail(String remetente, String destinatario, String titulo, String mensagem) {		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(remetente);
		mailMessage.setTo(destinatario);
		mailMessage.setSubject(titulo);
		mailMessage.setText(mensagem);
		mailSender.send(mailMessage);
	}

}
