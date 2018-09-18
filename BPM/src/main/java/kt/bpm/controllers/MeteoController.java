package kt.bpm.controllers;

import com.mashape.unirest.http.exceptions.UnirestException;
import kt.bpm.models.MeteoAnswer;
import kt.bpm.models.MeteoMessage;
import kt.bpm.services.CamundaRestService;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MeteoController {
    @Autowired
    private CamundaRestService camundaRestService;

    @MessageMapping("/meteo")
    public void meteo(MeteoMessage message, MeteoMessage meteoMessage) throws UnirestException {
        Map<String, String> value = new HashMap<>();

        value.put("type", "String");
        value.put("value", meteoMessage.getCp().trim().replaceAll(" ", ""));

        String pid = (String) camundaRestService.startProcess("Process_1", meteoMessage).getBody().getObject().get("id");
        camundaRestService.completeTask(Collections.singletonMap("variables",
                Collections.singletonMap("postalCode", value)),
                    camundaRestService.getTaskId(pid, 0));
    }
}
