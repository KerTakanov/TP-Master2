package kt.bpm.camunda.impl;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import kt.bpm.camunda.StateHandler;
import kt.bpm.models.MeteoAnswer;
import kt.bpm.models.meteo.Condition;
import kt.bpm.models.meteo.Meteo;
import kt.bpm.models.meteo.Title;
import kt.bpm.services.CamundaRestService;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GetMeteoState extends StateHandler {
    @Override
    protected String topicname() {
        return "get-meteo";
    }

    @Override
    protected void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) throws UnirestException {
        String CP = (String) context.get("postalCode");
        JsonNode json = null;
        json = Unirest.get("https://query.yahooapis.com/v1/public/yql?q=select%20item.title,%20item.condition%20from" +
                "%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" + CP + "%22)&format=json")
                .header("content-type", "application/json")
                .asJson()
                .getBody();

        String conditionString = json.getObject().getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONObject("condition").toString();
        String titleString = json.getObject().getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getString("title");

        ObjectMapper mapper = new ObjectMapper();
        System.out.println("REEEEEEEE");
        try {
            Condition condition = mapper.readValue(conditionString, Condition.class);

            Meteo meteo = new Meteo();
            meteo.setCondition(condition);

            output.put("result", String.format("%s; %s at %s °C", titleString, meteo.getCondition().getDate(), meteo.getCondition().getTemp()));

            System.out.println("Sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
