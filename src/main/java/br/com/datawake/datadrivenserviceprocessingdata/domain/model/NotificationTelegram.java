package br.com.datawake.datadrivenserviceprocessingdata.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationTelegram {

    private String token;
    private String chatId;
}
