package kt.bpm.camunda.impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import kt.bpm.camunda.StateHandler;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
public class GetMeteoState extends StateHandler {
    @Override
    protected String topicname() {
        return "meteo";
    }

    @Override
    protected void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) throws UnirestException {
        String CP = (String) context.get("postalCode");


    }
}
