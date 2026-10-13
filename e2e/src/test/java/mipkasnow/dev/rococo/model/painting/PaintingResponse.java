package mipkasnow.dev.rococo.model.painting;

import mipkasnow.dev.rococo.model.artist.ArtistResponse;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;

import java.util.UUID;

public record PaintingResponse(
        UUID id,
        String title,
        String description,
        String content,
        MuseumResponse museum,
        ArtistResponse artist
) {
}
