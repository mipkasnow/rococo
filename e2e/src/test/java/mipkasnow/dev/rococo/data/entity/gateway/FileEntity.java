package mipkasnow.dev.rococo.data.entity.gateway;

import java.util.UUID;

public record FileEntity(
        UUID id,
        String objectKey,
        String bucket,
        String fileName,
        String contentType,
        long sizeBytes,
        String sha256
) {
}
