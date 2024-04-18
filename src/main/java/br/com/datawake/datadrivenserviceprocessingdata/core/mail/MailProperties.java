package br.com.datawake.datadrivenserviceprocessingdata.core.mail;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
@Component
@ConfigurationProperties("datawake.email")
public class MailProperties {

	@NotNull
	private String sender;
	
}