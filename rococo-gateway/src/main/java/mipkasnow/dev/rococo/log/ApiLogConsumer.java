package mipkasnow.dev.rococo.log;

import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.service.ApiLogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiLogConsumer {

    private final ApiLogService apiLogService;

    @KafkaListener(topics = ApiLogProducer.TOPIC)
    public void consume(ApiLogEvent event) {
        apiLogService.save(event);
    }
}
