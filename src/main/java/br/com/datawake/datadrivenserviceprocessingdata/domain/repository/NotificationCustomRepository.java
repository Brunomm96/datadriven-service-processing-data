package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.NotificationEmail;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.NotificationTelegram;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationCustomRepository {

    List<NotificationTelegram> listNotificationTelegram(String db, Long helpChainId);
    List<NotificationEmail> listNotificationEmail(String db, Long helpChainId);

}
