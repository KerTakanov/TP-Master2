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

@Controller
public class MeteoController {
    @Autowired
    private CamundaRestService camundaRestService;

    @MessageMapping("/meteo")
    public void meteo(MeteoMessage message, MeteoMessage meteoMessage) throws UnirestException {
        camundaRestService.startProcess("Process_1", meteoMessage);
    }
}
