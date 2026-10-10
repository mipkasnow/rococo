package mipkasnow.dev.rococo.integration.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import mipkasnow.dev.rococo.entity.MinioBucket;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

@TestConfiguration(proxyBeanMethods = false)
public class IntegrationInfrastructure {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:16-alpine");
    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("apache/kafka:4.1.1");
    private static final DockerImageName MINIO_IMAGE = DockerImageName
            .parse("pgsty/silo:RELEASE.2026-09-16T00-00-00Z")
            .asCompatibleSubstituteFor("minio/minio");

    private static PostgreSQLContainer<?> postgres;
    private static KafkaContainer kafka;
    private static MinIOContainer minio;

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        if (postgres == null || !postgres.isRunning()) {
            postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE);
        }
        return postgres;
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafka() {
        if (kafka == null || !kafka.isRunning()) {
            kafka = new KafkaContainer(KAFKA_IMAGE);
        }
        return kafka;
    }

    @Bean
    MinIOContainer minio() {
        if (minio == null || !minio.isRunning()) {
            minio = new MinIOContainer(MINIO_IMAGE)
                    .withUserName("user")
                    .withPassword("password");
            minio.start();
        }
        return minio;
    }

    @Bean
    DynamicPropertyRegistrar rococoS3Properties(MinIOContainer minio) {
        return registry -> {
            registry.add("rococo.s3.endpoint", minio::getS3URL);
            registry.add("rococo.s3.region", () -> "us-east-1");
            registry.add("rococo.s3.access-key", minio::getUserName);
            registry.add("rococo.s3.secret-key", minio::getPassword);
        };
    }

    @Bean
    ApplicationRunner minioBuckets(S3Client s3Client) {
        return args -> {
            for (MinioBucket bucket : MinioBucket.values()) {
                createBucketIfAbsent(s3Client, bucket.name());
            }
        };
    }

    @Bean
    ApiClient apiClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        return new ApiClient(mockMvc, objectMapper);
    }

    @Bean
    KafkaLogReader kafkaLogReader(KafkaContainer kafka) {
        return new KafkaLogReader(kafka);
    }

    private static void createBucketIfAbsent(S3Client client, String name) {
        try {
            client.createBucket(request -> request.bucket(name));
        } catch (S3Exception exception) {
            if (exception.statusCode() != 409) {
                throw exception;
            }
        }
    }
}
