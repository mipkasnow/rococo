package mipkasnow.dev.rococo.dto.museum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mipkasnow.dev.rococo.dto.geo.GeoRequest;
import mipkasnow.dev.rococo.validation.ValidPhoto;

import java.util.UUID;

public record MuseumUpdateRequest(
        @NotNull(message = "Идентификатор музея обязателен")
        UUID id,

        @NotBlank(message = "Название музея обязательно")
        @Size(min = 3, message = "Название музея не может быть короче 3 символов")
        @Size(max = 255, message = "Название музея не может быть длиннее 255 символов")
        String title,

        @NotBlank(message = "Описание музея обязательно")
        @Size(min = 10, message = "Описание музея не может быть короче 10 символов")
        @Size(max = 1000, message = "Описание музея не может быть длиннее 1000 символов")
        String description,

        @ValidPhoto(message = "Фотография музея обязательна")
        String photo,

        @NotNull(message = "Укажите страну нахождения музея")
        @Valid
        GeoRequest geo
) {

    @JsonIgnore
    public UUID getCountryId() {
        return geo.country().id();
    }
}
