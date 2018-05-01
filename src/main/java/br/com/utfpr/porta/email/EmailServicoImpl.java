package br.com.utfpr.porta.email;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailServicoImpl implements EmailServico {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmailServicoImpl.class);
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Override
	public void enviarEmail(String destinatario, String titulo, String mensagem) {
		
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mmh = new MimeMessageHelper(mimeMessage, true);
			mmh.setTo(destinatario);
			mmh.setSubject(titulo);
			mmh.setSentDate(new Date(System.currentTimeMillis()));
			mmh.setText(mensagem, true);
			javaMailSender.send(mimeMessage);
		} catch (MailException | MessagingException e) {
			LOG.error("Erro ao enviar e-mail", e);
		} 
	}

}
