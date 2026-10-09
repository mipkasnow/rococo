package mipkasnow.dev.rococo.dto.painting;

import mipkasnow.dev.rococo.dto.artist.ArtistResponse;
import mipkasnow.dev.rococo.dto.museum.MuseumResponse;

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
