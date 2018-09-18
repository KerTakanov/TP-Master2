package kt.bpm.camunda.impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import kt.bpm.camunda.StateHandler;
import kt.bpm.models.MeteoAnswer;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SendMeteoState extends StateHandler {
    @Override
    protected String topicname() {
        return "send-meteo";
    }

    @Autowired
    private SimpMessagingTemplate template;

    @Override
    protected void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) throws UnirestException {
        System.out.println("ayyyyyyy");
        template.convertAndSend("/topic/meteo", new MeteoAnswer("mes couilles"));
    }
}
