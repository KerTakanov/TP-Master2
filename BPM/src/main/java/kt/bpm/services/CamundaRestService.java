package kt.bpm.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.utils.MapUtil;
import kt.bpm.models.MeteoMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CamundaRestService {
    @Value("${camunda.url}")
    String CAMUNDA_URL;
    @Value("${camunda.rest.process.start}")
    String START_PROCESS_URL;
    @Value("${camunda.rest.task.complete}")
    String COMPLETE_TASK_URL;
    @Value("${camunda.rest.task.get.processInstance}")
    String GET_TASK_ID_URL;
    @Value("${camunda.rest.task.delete}")
    String DELETE_TASK_URL;

    public HttpResponse<JsonNode> startProcess(String processname, MeteoMessage meteoMessage) throws UnirestException {
        System.out.println("start process rest");

        return Unirest.post(CAMUNDA_URL + String.format(START_PROCESS_URL, processname))
                .header("Content-Type", "application/json")
                .asJson();
    }

    public String getTaskId(String processid, int i) throws UnirestException {
        System.out.println("get task id rest");
        HttpResponse<JsonNode> getTaskIdResponse = Unirest.get(CAMUNDA_URL + String.format(GET_TASK_ID_URL, processid))
                .asJson();

        return (String) getTaskIdResponse.getBody().getArray().getJSONObject(i).get("id");
    }

    public HttpResponse<JsonNode> completeTask(Object object, String taskId) throws UnirestException {
        if (object == null) {
            return Unirest.post(CAMUNDA_URL + String.format(COMPLETE_TASK_URL, taskId))
                    .header("Content-Type", "application/json")
                    .body("{}")
                    .asJson();
        } else {
            System.out.println("complete task rest");
            System.out.println(object.toString());
            System.out.println(taskId);
            return Unirest.post(CAMUNDA_URL + String.format(COMPLETE_TASK_URL, taskId))
                    .header("Content-Type", "application/json")
                    .body(object)
                    .asJson();
        }
    }

    public HttpResponse<JsonNode> deleteTask(String taskId) throws UnirestException {
        return Unirest.delete(CAMUNDA_URL + String.format(DELETE_TASK_URL, taskId))
                .header("Content-Type", "application/json")
                .asJson();
    }

}
