package mipkasnow.dev.rococo.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogProducer {

    public static final String TOPIC = "rococo.logs";

    private final KafkaTemplate<String, ApiLogEvent> kafkaTemplate;

    public void publish(ApiLogEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event.username(), event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to publish API log: {}", exception.toString());
                        }
                    });
        } catch (Exception exception) {
            log.error("Failed to publish API log: {}", exception.toString());
        }
    }
}
