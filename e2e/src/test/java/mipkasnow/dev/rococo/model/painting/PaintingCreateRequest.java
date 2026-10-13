package mipkasnow.dev.rococo.model.painting;

import mipkasnow.dev.rococo.model.artist.ArtistIdRequest;
import mipkasnow.dev.rococo.model.museum.MuseumIdRequest;

public record PaintingCreateRequest(
        String title,
        String description,
        String content,
        ArtistIdRequest artist,
        MuseumIdRequest museum
) {
}
