package mipkasnow.dev.rococo.data.entity.gateway;

import java.util.UUID;

public record ArtistEntity(
        UUID id,
        String name,
        String biography,
        UUID photoFileId
) {
}
