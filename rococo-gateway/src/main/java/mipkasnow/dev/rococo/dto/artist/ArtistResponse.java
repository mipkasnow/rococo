package mipkasnow.dev.rococo.dto.artist;

import mipkasnow.dev.rococo.entity.ArtistEntity;

import java.util.UUID;

public record ArtistResponse(
        UUID id,
        String name,
        String biography,
        String photo
) {

    public ArtistResponse(ArtistEntity artistEntity, String photo) {
        this(
                artistEntity.getId(),
                artistEntity.getName(),
                artistEntity.getBiography(),
                photo
        );
    }
}
