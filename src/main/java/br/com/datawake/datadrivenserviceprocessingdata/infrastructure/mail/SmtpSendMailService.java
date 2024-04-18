package br.com.datawake.datadrivenserviceprocessingdata.infrastructure.mail;

import br.com.datawake.datadrivenserviceprocessingdata.core.mail.MailProperties;
import br.com.datawake.datadrivenserviceprocessingdata.domain.service.SendMailService;
import br.com.datawake.datadrivenserviceprocessingdata.infrastructure.exception.MailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class SmtpSendMailService implements SendMailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailProperties mailProperties;
	
	@Override
	public void send(Message message) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(mailProperties.getSender());
			helper.setTo(message.getRecipients().toArray(new String[0]));
			helper.setSubject(message.getSubject());
			helper.setText(message.getBody(), true);
			
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			throw new MailException("Não foi possível enviar e-mail", e);
		}
	}

}