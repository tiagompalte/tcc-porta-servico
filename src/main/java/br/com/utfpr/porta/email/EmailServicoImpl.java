package br.com.utfpr.porta.email;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.com.utfpr.porta.modelo.Parametro;
import br.com.utfpr.porta.modelo.TokenResetSenha;
import br.com.utfpr.porta.repositorio.Parametros;
import br.com.utfpr.porta.servico.TokenResetSenhaServico;

public class EmailServicoImpl implements EmailServico {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmailServicoImpl.class);
	
	private TemplateEngine templateEngine;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private TokenResetSenhaServico tokenResetSenhaServico;
	
	@Autowired
	private Parametros parametroRepositorio;
	
	public EmailServicoImpl(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}
	
	@Override
	public void enviarEmailResetSenha(String email) {
		
		try {			
			TokenResetSenha token = tokenResetSenhaServico.gravarToken(email);
						
			if(token != null && token.getUsuario() != null && Strings.isNotEmpty(token.getToken())) {
				
				Parametro parUrl = parametroRepositorio.findOne("URL_RESET_SENHA");
				if(parUrl == null || Strings.isEmpty(parUrl.getValor())) {
					throw new NullPointerException("Parâmetro URL_RESET_SENHA não cadastrado");
				}
				
				String url = parUrl.getValor().endsWith("/") ? parUrl.getValor() : parUrl.getValor().concat("/");
				
				Context context = new Context();
				context.setVariable("url", url.concat(token.getToken()));
				context.setVariable("usuario", token.getUsuario());
				String mensagem = templateEngine.process("email/mensagemResetSenha", context);
				envioEmail(token.getUsuario().getEmail(), "Alterar Senha", mensagem);
			}
			
		} catch(Exception e) {
			LOG.error("Erro ao enviar email para resetar senha", e);
		}		
	}
		
	private void envioEmail(String destinatario, String titulo, String mensagem) throws MessagingException {		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mmh = new MimeMessageHelper(mimeMessage, true);
		mmh.setTo(destinatario);
		mmh.setSubject(titulo);
		mmh.setSentDate(new Date(System.currentTimeMillis()));
		mmh.setText(mensagem, true);
		javaMailSender.send(mimeMessage);
	}

}
