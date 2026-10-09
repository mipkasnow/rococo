package mipkasnow.dev.rococo.dto.painting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mipkasnow.dev.rococo.dto.artist.ArtistIdRequest;
import mipkasnow.dev.rococo.dto.museum.MuseumIdRequest;
import mipkasnow.dev.rococo.validation.ValidPhoto;

import java.util.UUID;

public record PaintingCreateRequest(
        @NotBlank(message = "Название картины обязательно")
        @Size(min = 3, message = "Название картины не может быть короче 3 символов")
        @Size(max = 255, message = "Название картины не может быть длиннее 255 символов")
        String title,

        @NotBlank(message = "Описание картины обязательно")
        @Size(min = 10, message = "Описание картины не может быть короче 10 символов")
        @Size(max = 1000, message = "Описание картины не может быть длиннее 1000 символов")
        String description,

        @ValidPhoto(message = "Изображение картины обязательно")
        String content,

        @NotNull(message = "Укажите автора картины")
        @Valid
        ArtistIdRequest artist,

        @Valid
        MuseumIdRequest museum
) {

    @JsonIgnore
    public UUID getArtistId() {
        return artist.id();
    }

    @JsonIgnore
    public UUID getMuseumId() {
        return museum == null ? null : museum.id();
    }
}
