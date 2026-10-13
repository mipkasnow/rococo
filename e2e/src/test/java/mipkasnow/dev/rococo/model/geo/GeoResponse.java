package mipkasnow.dev.rococo.model.geo;

import mipkasnow.dev.rococo.model.country.CountryDto;

public record GeoResponse(String city, CountryDto country) {
}
