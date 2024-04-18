package br.com.datawake.datadrivenserviceprocessingdata.domain.utils;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.NotificationEmail;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.NotificationTelegram;
import br.com.datawake.datadrivenserviceprocessingdata.domain.service.SendMailService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class DatadrivenUtils {

    public static String getNewAccessKey() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    public static void sendTelegram(WebClient webClientTelegram, NotificationTelegram notificationTelegram, String message) {

        if ( webClientTelegram != null ) {

            log.info("Enviando notificação via Telegram para chatId" + notificationTelegram.getChatId());
            log.info(message);

            try {

                message = message.replace("<br>", "\n");
                message = message.replace("<b>", "*");
                message = message.replace("</b>", "*");
                message = message.replace("-", "\\-");
                message = message.replace(".", "\\.");
                message = message.replace("(", "\\(");
                message = message.replace(")", "\\)");

                webClientTelegram
                        .get()
                        .uri("/bot" + notificationTelegram.getToken().trim() + "/sendMessage?parse_mode=MarkdownV2&chat_id=" + notificationTelegram.getChatId() + "&text=" + message)
                        .retrieve()
                        .toBodilessEntity().block();

            } catch (WebClientResponseException e) {
                String responseBody = e.getResponseBodyAsString();
                JSONObject json = new JSONObject(responseBody);
                log.info("Falha no envio de notificação via Telegram");
                log.info(json.toString());
            }

        }

    }

    public static void sendMail(SendMailService sendMailService, NotificationEmail notificationEmail, String message) {

        if ( notificationEmail.getSendTo().isEmpty() )
            log.info("E-mail não enviado pois não foi encontrado destinatário configurado!");
        else
            log.info("Enviando notificação de e-mail para " + notificationEmail.getSendTo());

        log.info(message);

        if ( !notificationEmail.getSendTo().isEmpty() ) {

            var mail = SendMailService.Message.builder()
                    .subject(notificationEmail.getSubject())
                    .body(message)
                    .recipient(notificationEmail.getSendTo())
                    .build();

            sendMailService.send(mail);

        }
    }


    public List<Map<String, String>> queryToMap(List<Object[]> queryResp, String[] columns) {
        List<Map<String,String>> dataList = new ArrayList<>();

        for(Object[] obj : queryResp) {
            Map<String,String> row = new HashMap<>(columns.length);
            for(int i = 0; i< columns.length; i++) {
                if(obj[i]!=null)
                    row.put(columns[i], obj[i].toString());
                else
                    row.put(columns[i], "");
            }
            dataList.add(row);
        }
        return dataList;
    }

}
