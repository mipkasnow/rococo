package mipkasnow.dev.rococo.model.museum;

import mipkasnow.dev.rococo.model.geo.GeoRequest;

public record MuseumCreateRequest(
        String title,
        String description,
        String photo,
        GeoRequest geo
) {
}
