package mipkasnow.dev.rococo.model.geo;

import mipkasnow.dev.rococo.model.country.CountryRequest;

public record GeoRequest(String city, CountryRequest country) {
}
