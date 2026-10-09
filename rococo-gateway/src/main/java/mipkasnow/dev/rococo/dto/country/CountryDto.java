package mipkasnow.dev.rococo.dto.country;

import mipkasnow.dev.rococo.entity.CountryEntity;

import java.util.UUID;

public record CountryDto(UUID id, String name) {

    public CountryDto(CountryEntity countryEntity) {
        this(countryEntity.getId(), countryEntity.getName());
    }
}
