package mipkasnow.dev.rococo.dto.artist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mipkasnow.dev.rococo.validation.ValidPhoto;

public record ArtistCreateRequest(
        @NotBlank(message = "Имя художника обязательно")
        @Size(min = 3, message = "Имя художника не может быть короче 3 символов")
        @Size(max = 255, message = "Имя художника не может быть длиннее 255 символов")
        String name,

        @NotBlank(message = "Биография художника обязательна")
        @Size(min = 10, message = "Биография художника не может быть короче 10 символов")
        @Size(max = 2000, message = "Биография художника не может быть длиннее 2000 символов")
        String biography,

        @ValidPhoto(message = "Фотография художника обязательна")
        String photo
) {
}
