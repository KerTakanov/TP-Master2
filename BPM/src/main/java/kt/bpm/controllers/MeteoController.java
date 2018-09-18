package kt.bpm.controllers;

import kt.bpm.models.MeteoAnswer;
import kt.bpm.models.MeteoMessage;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MeteoController {
    @Autowired
    private ExternalTaskClient client;

    @MessageMapping("/meteo")
    public void meteo(MeteoMessage message) {
        // Démarrer le process

    }
}
