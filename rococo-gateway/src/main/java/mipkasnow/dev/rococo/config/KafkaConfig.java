package mipkasnow.dev.rococo.config;

import mipkasnow.dev.rococo.log.ApiLogProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic apiLogsTopic() {
        return TopicBuilder.name(ApiLogProducer.TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
