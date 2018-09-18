package kt.bpm.camunda.impl;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import kt.bpm.camunda.StateHandler;
import kt.bpm.models.meteo.Condition;
import kt.bpm.models.meteo.Meteo;
import kt.bpm.models.meteo.Title;
import kt.bpm.services.CamundaRestService;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GetMeteoState extends StateHandler {
    @Override
    protected String topicname() {
        return "get-meteo";
    }

    @Override
    protected void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) throws UnirestException {
        String CP = (String) context.get("postalCode");
        JsonNode json = Unirest.get("https://query.yahooapis.com/v1/public/yql?q=select item.title, item.condition from weather.forecast where woeid in (select woeid from geo.places(1) where text%3D\"" + CP + "\")&format=json")
        .header("content-type", "application/json")
        .asJson()
        .getBody();

        String conditionString = json.getObject().getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONObject("condition").toString();
        String titleString = json.getObject().getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONObject("title").toString();

        ObjectMapper mapper = new ObjectMapper();
        try {
            Condition condition = mapper.readValue(conditionString, Condition.class);
            Title title = mapper.readValue(titleString, Title.class);

            Meteo meteo = new Meteo();
            meteo.setCondition(condition);
            meteo.setTitle(title);

            output.put("result", meteo);

        }catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
