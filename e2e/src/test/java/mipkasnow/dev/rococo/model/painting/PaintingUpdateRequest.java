package mipkasnow.dev.rococo.model.painting;

import mipkasnow.dev.rococo.model.artist.ArtistIdRequest;
import mipkasnow.dev.rococo.model.museum.MuseumIdRequest;

import java.util.UUID;

public record PaintingUpdateRequest(
        UUID id,
        String title,
        String description,
        String content,
        ArtistIdRequest artist,
        MuseumIdRequest museum
) {
}
