package mipkasnow.dev.rococo.data.entity.gateway;

import java.util.UUID;

public record MuseumEntity(
        UUID id,
        String title,
        String description,
        String city,
        UUID countryId,
        UUID photoFileId
) {
}
