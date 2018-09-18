package kt.bpm.controllers;

import kt.bpm.models.MeteoAnswer;
import kt.bpm.models.MeteoMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MeteoController {
    @MessageMapping("/meteo")
    @SendTo("/topic/meteo")
    public MeteoAnswer meteo(MeteoMessage message) {
        return new MeteoAnswer("Il fera beau");
    }
}
