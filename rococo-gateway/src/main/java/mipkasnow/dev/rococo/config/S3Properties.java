package mipkasnow.dev.rococo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rococo.s3")
public record S3Properties(
        String endpoint,
        String region,
        String accessKey,
        String secretKey
) {
}
