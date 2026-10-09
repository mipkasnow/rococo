package mipkasnow.dev.rococo.dto.country;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CountryRequest(
        @NotNull(message = "Укажите страну нахождения музея")
        UUID id
) {
}
