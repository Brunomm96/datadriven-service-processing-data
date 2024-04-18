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
@ConfigurationProperties("datawake.locale")
public class LocaleProperties {

	@NotNull
	private String local;
	
}