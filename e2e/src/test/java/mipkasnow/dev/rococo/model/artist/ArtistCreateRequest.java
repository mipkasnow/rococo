package mipkasnow.dev.rococo.model.artist;

public record ArtistCreateRequest(
        String name,
        String biography,
        String photo
) {
}
