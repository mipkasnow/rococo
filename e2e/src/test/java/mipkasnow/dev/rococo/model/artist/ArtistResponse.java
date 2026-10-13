package mipkasnow.dev.rococo.model.artist;

import java.util.UUID;

public record ArtistResponse(
        UUID id,
        String name,
        String biography,
        String photo
) {
}
