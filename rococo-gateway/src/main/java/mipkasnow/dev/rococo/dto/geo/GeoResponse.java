package mipkasnow.dev.rococo.dto.geo;

import mipkasnow.dev.rococo.dto.country.CountryDto;

public record GeoResponse(
        String city,
        CountryDto country
) {
}
