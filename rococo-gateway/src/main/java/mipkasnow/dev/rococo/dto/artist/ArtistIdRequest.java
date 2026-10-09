package mipkasnow.dev.rococo.dto.artist;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ArtistIdRequest(
        @NotNull(message = "Укажите автора картины")
        UUID id
) {
}
