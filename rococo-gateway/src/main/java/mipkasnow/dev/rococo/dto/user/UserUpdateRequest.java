package mipkasnow.dev.rococo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mipkasnow.dev.rococo.validation.ValidPhoto;

import java.util.UUID;

public record UserUpdateRequest(
        @NotNull(message = "Идентификатор пользователя обязателен")
        UUID id,

        @NotBlank(message = "Логин пользователя обязателен")
        @Size(max = 50, message = "Логин пользователя не может быть длиннее 50 символов")
        String username,

        @Size(max = 255, message = "Имя пользователя не может быть длиннее 255 символов")
        String firstname,

        @Size(max = 255, message = "Фамилия пользователя не может быть длиннее 255 символов")
        String lastname,

        @ValidPhoto(required = false)
        String avatar
) {
}
