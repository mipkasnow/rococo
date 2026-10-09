package mipkasnow.dev.rococo.dto.geo;

import jakarta.validation.Valid;
import mipkasnow.dev.rococo.dto.country.CountryRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GeoRequest(
        @NotBlank(message = "Город музея обязателен")
        @Size(min = 3, message = "Город музея не может быть короче 3 символов")
        @Size(max = 255, message = "Город музея не может быть длиннее 255 символов")
        String city,

        @NotNull(message = "Укажите страну нахождения музея")
        @Valid
        CountryRequest country
) {
}
