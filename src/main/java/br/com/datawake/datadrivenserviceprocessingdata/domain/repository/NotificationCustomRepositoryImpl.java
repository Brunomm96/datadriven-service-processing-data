package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConsumptionOutOfBound;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.NotificationEmail;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.NotificationTelegram;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;



    private List<Map<String, String>> toMap(List<Object[]> queryResp, String[] columns) {
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

    @Override
    public List<NotificationTelegram> listNotificationTelegram(String db, Long helpChainId) {

        List<NotificationTelegram> listNotificationTelegram = new ArrayList<NotificationTelegram>();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT ");
        sb.append("act.token");
        sb.append(", act.chat_id ");
        sb.append("FROM ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_consumption_monitoring]");
        sb.append(" cm ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_help_chain] hc ON hc.id = cm.help_chain_id ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_help_chain_hierarchy_level] hchl ON hchl.help_chain_id = hc.id ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_help_chain_hierarchy_level_dw_help_chain_action] hchlhca ON hchlhca.help_chain_hierarchy_level_id = hchl.id ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_action_config] ac ON ac.help_chain_action_id = hchlhca.help_chain_action_id ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_action_config_telegram] act ON act.id = ac.action_config_telegram_id ");
        sb.append("WHERE hc.id = :helpChainId ");

        List<Object[]> queryResp = entityManager.createNativeQuery(sb.toString()).setParameter("helpChainId", helpChainId).getResultList();

        String[] columns = {
                "token"
                ,"chat_id"
        };

        List<Map<String, String>> dataList = toMap(queryResp, columns);

        for ( Map<String,String> record : dataList ) {
            listNotificationTelegram.add(getNotificationTelegramResult(record));
        }

        return listNotificationTelegram;

    }

    private NotificationTelegram getNotificationTelegramResult(Map<String, String> record) {

        NotificationTelegram notificationTelegram = new NotificationTelegram();

        if ( record.get("ErrorMessage") == null ) {
            notificationTelegram.setToken(record.get("token"));
            notificationTelegram.setChatId(record.get("chat_id"));
        }

        return notificationTelegram;

    }

    @Override
    public List<NotificationEmail> listNotificationEmail(String db, Long helpChainId) {

        List<NotificationEmail> listNotificationEmail = new ArrayList<NotificationEmail>();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT ");
        sb.append("ace.subject");
        sb.append(", ace.send_to ");
        sb.append("FROM ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_consumption_monitoring]");
        sb.append(" cm ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_help_chain] hc ON hc.id = cm.help_chain_id ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_help_chain_hierarchy_level] hchl ON hchl.help_chain_id = hc.id ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_help_chain_hierarchy_level_dw_help_chain_action] hchlhca ON hchlhca.help_chain_hierarchy_level_id = hchl.id ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_action_config] ac ON ac.help_chain_action_id = hchlhca.help_chain_action_id ");
        sb.append("INNER JOIN ");
        sb.append("[");
        sb.append( db );
        sb.append("].[dbo].");
        sb.append("[dw_action_config_email] ace ON ace.id = ac.action_config_email_id ");
        sb.append("WHERE hc.id = :helpChainId ");

        List<Object[]> queryResp = entityManager.createNativeQuery(sb.toString()).setParameter("helpChainId", helpChainId).getResultList();

        String[] columns = {
                "subject"
                ,"send_to"
        };

        List<Map<String, String>> dataList = toMap(queryResp, columns);

        for ( Map<String,String> record : dataList ) {
            listNotificationEmail.add(getNotificationEmailResult(record));
        }

        return listNotificationEmail;
    }

    private NotificationEmail getNotificationEmailResult(Map<String, String> record) {

        NotificationEmail notificationEmail = new NotificationEmail();

        if ( record.get("ErrorMessage") == null ) {
            notificationEmail.setSubject(record.get("subject"));
            notificationEmail.setSendTo(record.get("send_to"));
        }

        return notificationEmail;

    }
}
