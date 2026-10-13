package mipkasnow.dev.rococo.model.museum;

import mipkasnow.dev.rococo.model.geo.GeoResponse;

import java.util.UUID;

public record MuseumResponse(
        UUID id,
        String title,
        String description,
        String photo,
        GeoResponse geo
) {
}
