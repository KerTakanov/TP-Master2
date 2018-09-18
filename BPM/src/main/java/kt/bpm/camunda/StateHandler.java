package kt.bpm.camunda;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Component
public abstract class StateHandler {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    @NonNull
    protected ExternalTaskHandler handler;
    protected Map<String, Object> context;
    protected Map<String, Object> output = new HashMap<>();
    @Autowired
    ExternalTaskClient client;

    @PostConstruct
    public void init() {
        log.info("Registering State `" + topicname() + "`.");
        client.subscribe(topicname()).handler(((externalTask, externalTaskService) -> {
            context = externalTask.getAllVariables();

            beforeExecute(externalTask, externalTaskService);
            try {
                execute(externalTask, externalTaskService);
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            afterExecute(externalTask, externalTaskService);

            externalTaskService.complete(externalTask, output);
        })).open();
    }

    protected abstract String topicname();

    protected void beforeExecute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    }

    protected abstract void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) throws UnirestException;

    protected void afterExecute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
    }

    protected <T> T get(String key) {
        return (T) context.get(key);
    }

    protected void put(String key, String value) { output.put(key,value); }

    protected <T> T get(String key, Object defaultValue) {
        return (T) context.getOrDefault(key, defaultValue);
    }
}
