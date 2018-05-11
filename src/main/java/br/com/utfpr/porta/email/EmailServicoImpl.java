package br.com.utfpr.porta.email;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import br.com.utfpr.porta.util.Util;

public class EmailServicoImpl implements EmailServico {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmailServicoImpl.class);
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Override
	public void enviarEmail(String destinatario, String titulo, String mensagem) {
		
		String mensagemIso = null;		
		try {
			mensagemIso = Util.converterUTF8toISO88591(mensagem);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Erro ao enviar e-mail", e);
		}	
		
		if(Strings.isEmpty(mensagemIso)) {
			return;
		}
		
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mmh = new MimeMessageHelper(mimeMessage, true);
			mmh.setTo(destinatario);
			mmh.setSubject(titulo);
			mmh.setSentDate(new Date(System.currentTimeMillis()));
			mmh.setText(mensagemIso, true);
			javaMailSender.send(mimeMessage);
		} catch (MailException | MessagingException e) {
			LOG.error("Erro ao enviar e-mail", e);
		} 
	}

}
