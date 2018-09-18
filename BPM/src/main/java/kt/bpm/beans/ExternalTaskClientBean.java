package kt.bpm.beans;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.backoff.ExponentialBackoffStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalTaskClientBean {
    @Bean
    public ExternalTaskClient client() {
        return ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(1000)
                .backoffStrategy(new ExponentialBackoffStrategy(500, 1, 500))
                .lockDuration(5000)
                .build();
    }
}
