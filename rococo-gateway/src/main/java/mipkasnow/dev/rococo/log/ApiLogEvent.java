package mipkasnow.dev.rococo.log;

import java.time.Instant;
import java.util.UUID;

public record ApiLogEvent(
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
