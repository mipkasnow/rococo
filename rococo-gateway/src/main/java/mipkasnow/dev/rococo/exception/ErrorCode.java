package mipkasnow.dev.rococo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ArtistNotFound(HttpStatus.NOT_FOUND, "Художник не найден"),
    MuseumNotFound(HttpStatus.NOT_FOUND, "Музей не найден"),
    PaintingNotFound(HttpStatus.NOT_FOUND, "Картина не найдена"),
    CountryNotFound(HttpStatus.NOT_FOUND, "Страна не найдена"),
    UserNotFound(HttpStatus.NOT_FOUND, "Пользователь не найден"),
    ArtistNameTaken(HttpStatus.CONFLICT, "Художник с таким именем уже существует"),
    MuseumTitleTaken(HttpStatus.CONFLICT, "Музей с таким названием уже существует"),
    PaintingTitleTaken(HttpStatus.CONFLICT, "Картина с таким названием уже существует"),
    UserUpdateForbidden(HttpStatus.FORBIDDEN, "Нельзя изменить другого пользователя"),
    Unauthorized(HttpStatus.UNAUTHORIZED, "Нужна авторизация"),
    Forbidden(HttpStatus.FORBIDDEN, "Недостаточно прав"),
    BadRequest(HttpStatus.BAD_REQUEST, "Некорректное тело запроса"),
    InvalidImage(HttpStatus.BAD_REQUEST, "Некорректное изображение"),
    FileStorage(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось обратиться к хранилищу файлов"),
    Unexpected(HttpStatus.INTERNAL_SERVER_ERROR, "Что-то пошло не так");

    private final HttpStatus status;
    private final String message;
}
