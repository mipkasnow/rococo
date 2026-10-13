package mipkasnow.dev.rococo.model.museum;

import mipkasnow.dev.rococo.model.geo.GeoRequest;

import java.util.UUID;

public record MuseumUpdateRequest(
        UUID id,
        String title,
        String description,
        String photo,
        GeoRequest geo
) {
}
