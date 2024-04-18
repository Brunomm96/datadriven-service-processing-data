package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationEmail {

    private String subject;
    private String sendTo;
}
