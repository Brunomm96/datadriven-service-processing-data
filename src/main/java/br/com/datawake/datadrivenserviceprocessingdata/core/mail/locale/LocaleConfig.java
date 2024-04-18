package br.com.datawake.datadrivenserviceprocessingdata.core.mail.locale;

import br.com.datawake.datadrivenserviceprocessingdata.core.mail.LocaleProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Slf4j
@Configuration
public class LocaleConfig {

    @Autowired
    private LocaleProperties localeProperties;

    @PostConstruct
    public void init() {
        String timeZone = localeProperties.getLocal();
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        log.info("Setting time zone to [" + timeZone + "]");
    }
}