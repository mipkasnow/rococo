package mipkasnow.dev.rococo.integration.support;

import mipkasnow.dev.rococo.log.ApiLogEvent;
import mipkasnow.dev.rococo.log.ApiLogProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.testcontainers.kafka.KafkaContainer;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class KafkaLogReader implements DisposableBean {

    private final KafkaConsumer<String, ApiLogEvent> consumer;
    private final List<ApiLogEvent> events = new CopyOnWriteArrayList<>();
    private boolean subscribed;

    public KafkaLogReader(KafkaContainer kafka) {
        JsonDeserializer<ApiLogEvent> valueDeserializer = new JsonDeserializer<>(ApiLogEvent.class, false);
        valueDeserializer.addTrustedPackages(ApiLogEvent.class.getPackageName());
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "rococo-it-logs");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        consumer = new KafkaConsumer<>(properties, new StringDeserializer(), valueDeserializer);
    }

    public ApiLogEvent await(String method, String path, int status, String username, Instant after) {
        Awaitility.await().atMost(15, SECONDS)
                .pollInterval(Duration.ofMillis(200))
                .until(() -> find(method, path, status, username, after).isPresent());
        return find(method, path, status, username, after).orElseThrow();
    }

    @Override
    public void destroy() {
        consumer.close(Duration.ofSeconds(2));
    }

    private synchronized Optional<ApiLogEvent> find(String method, String path, int status, String username, Instant after) {
        poll();
        return events.stream()
                .filter(event -> method.equals(event.httpMethod()))
                .filter(event -> path.equals(event.path()))
                .filter(event -> event.httpStatus() == status)
                .filter(event -> username.equals(event.username()))
                .filter(event -> !event.occurredAt().isBefore(after))
                .findFirst();
    }

    private void poll() {
        if (!subscribed) {
            consumer.subscribe(List.of(ApiLogProducer.TOPIC));
            subscribed = true;
        }
        consumer.poll(Duration.ofMillis(200)).forEach(record -> events.add(record.value()));
    }
}
