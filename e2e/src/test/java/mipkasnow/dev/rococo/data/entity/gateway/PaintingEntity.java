package mipkasnow.dev.rococo.data.entity.gateway;

import java.util.UUID;

public record PaintingEntity(
        UUID id,
        String title,
        String description,
        UUID artistId,
        UUID museumId,
        UUID contentFileId
) {
}
