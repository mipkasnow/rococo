package mipkasnow.dev.rococo.data.entity.gateway;

import java.time.Instant;
import java.util.UUID;

public record LogEntity(
        UUID id,
        Instant occurredAt,
        String ip,
        String username,
        String httpMethod,
        String path,
        int httpStatus,
        boolean success,
        String error
) {
}
