package kt.bpm.camunda.impl;

import com.mashape.unirest.http.exceptions.UnirestException;
import kt.bpm.camunda.StateHandler;
import kt.bpm.services.CamundaRestService;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetMeteoState extends StateHandler {
    @Autowired
    private CamundaRestService camundaRestService;

    @Override
    protected String topicname() {
        return "meteo";
    }

    @Override
    protected void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) throws UnirestException {
        String CP = (String) context.get("postalCode");
    }
}