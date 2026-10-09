package mipkasnow.dev.rococo.dto.museum;

import mipkasnow.dev.rococo.dto.country.CountryDto;
import mipkasnow.dev.rococo.dto.geo.GeoResponse;
import mipkasnow.dev.rococo.entity.MuseumEntity;

import java.util.UUID;

public record MuseumResponse(
        UUID id,
        String title,
        String description,
        String photo,
        GeoResponse geo
) {

    public MuseumResponse(MuseumEntity museum, String photo) {
        this(
                museum.getId(),
                museum.getTitle(),
                museum.getDescription(),
                photo,
                new GeoResponse(museum.getCity(), new CountryDto(museum.getCountry()))
        );
    }
}
