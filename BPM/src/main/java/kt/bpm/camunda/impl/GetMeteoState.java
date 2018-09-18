package kt.bpm.camunda.impl;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import kt.bpm.camunda.StateHandler;
import kt.bpm.services.CamundaRestService;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetMeteoState extends StateHandler {
    @Override
    protected String topicname() {
        return "get-meteo";
    }

    @Override
    protected void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) throws UnirestException {
        String CP = (String) context.get("postalCode");
        output.put("result", Unirest.get("https://query.yahooapis.com/v1/public/yql?q=select item.title, item.condition from weather.forecast where woeid in (select woeid from geo.places(1) where text%3D\"73000\")&format=json")
        .header("content-type", "application/json")
        .asJson()
        .getBody());


    }
}
