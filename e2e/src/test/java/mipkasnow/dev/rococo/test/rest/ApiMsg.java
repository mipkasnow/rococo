package mipkasnow.dev.rococo.test.rest;

import lombok.experimental.UtilityClass;

@UtilityClass
class ApiMsg {

    static final String UNAUTHORIZED = "Нужна авторизация";
    static final String BAD_BODY = "Некорректное тело запроса";

    static final String ARTIST_NOT_FOUND = "Художник не найден";
    static final String MUSEUM_NOT_FOUND = "Музей не найден";
    static final String PAINTING_NOT_FOUND = "Картина не найдена";
    static final String COUNTRY_NOT_FOUND = "Страна не найдена";

    static final String ARTIST_NAME_TAKEN = "Художник с таким именем уже существует";
    static final String MUSEUM_TITLE_TAKEN = "Музей с таким названием уже существует";
    static final String PAINTING_TITLE_TAKEN = "Картина с таким названием уже существует";
    static final String USER_FORBIDDEN = "Нельзя изменить другого пользователя";

    static final String ARTIST_ID_REQUIRED = "Идентификатор художника обязателен";
    static final String ARTIST_NAME_REQUIRED = "Имя художника обязательно";
    static final String ARTIST_NAME_SHORT = "Имя художника не может быть короче 3 символов";
    static final String ARTIST_NAME_LONG = "Имя художника не может быть длиннее 255 символов";
    static final String ARTIST_BIO_REQUIRED = "Биография художника обязательна";
    static final String ARTIST_BIO_SHORT = "Биография художника не может быть короче 10 символов";
    static final String ARTIST_BIO_LONG = "Биография художника не может быть длиннее 2000 символов";
    static final String ARTIST_PHOTO_REQUIRED = "Фотография художника обязательна";

    static final String MUSEUM_ID_REQUIRED = "Идентификатор музея обязателен";
    static final String MUSEUM_TITLE_REQUIRED = "Название музея обязательно";
    static final String MUSEUM_TITLE_SHORT = "Название музея не может быть короче 3 символов";
    static final String MUSEUM_TITLE_LONG = "Название музея не может быть длиннее 255 символов";
    static final String MUSEUM_DESCRIPTION_REQUIRED = "Описание музея обязательно";
    static final String MUSEUM_DESCRIPTION_SHORT = "Описание музея не может быть короче 10 символов";
    static final String MUSEUM_DESCRIPTION_LONG = "Описание музея не может быть длиннее 1000 символов";
    static final String MUSEUM_PHOTO_REQUIRED = "Фотография музея обязательна";
    static final String CITY_REQUIRED = "Город музея обязателен";
    static final String CITY_SHORT = "Город музея не может быть короче 3 символов";
    static final String CITY_LONG = "Город музея не может быть длиннее 255 символов";
    static final String COUNTRY_REQUIRED = "Укажите страну нахождения музея";

    static final String PAINTING_ID_REQUIRED = "Идентификатор картины обязателен";
    static final String PAINTING_TITLE_REQUIRED = "Название картины обязательно";
    static final String PAINTING_TITLE_SHORT = "Название картины не может быть короче 3 символов";
    static final String PAINTING_TITLE_LONG = "Название картины не может быть длиннее 255 символов";
    static final String PAINTING_DESCRIPTION_REQUIRED = "Описание картины обязательно";
    static final String PAINTING_DESCRIPTION_SHORT = "Описание картины не может быть короче 10 символов";
    static final String PAINTING_DESCRIPTION_LONG = "Описание картины не может быть длиннее 1000 символов";
    static final String PAINTING_CONTENT_REQUIRED = "Изображение картины обязательно";
    static final String ARTIST_REQUIRED = "Укажите автора картины";

    static final String USER_ID_REQUIRED = "Идентификатор пользователя обязателен";
    static final String USERNAME_REQUIRED = "Логин пользователя обязателен";
    static final String USERNAME_LONG = "Логин пользователя не может быть длиннее 50 символов";
    static final String FIRSTNAME_LONG = "Имя пользователя не может быть длиннее 255 символов";
    static final String LASTNAME_LONG = "Фамилия пользователя не может быть длиннее 255 символов";

    static final String IMAGE_INVALID = "Некорректное изображение";
    static final String IMAGE_FORMAT = "Допустимые форматы изображения: PNG, JPEG, GIF, WEBP";

    static final String CODE_UNAUTHORIZED = "Unauthorized";
    static final String CODE_BAD_REQUEST = "BadRequest";
    static final String CODE_ARTIST_NOT_FOUND = "ArtistNotFound";
    static final String CODE_MUSEUM_NOT_FOUND = "MuseumNotFound";
    static final String CODE_PAINTING_NOT_FOUND = "PaintingNotFound";
    static final String CODE_COUNTRY_NOT_FOUND = "CountryNotFound";
    static final String CODE_ARTIST_NAME_TAKEN = "ArtistNameTaken";
    static final String CODE_MUSEUM_TITLE_TAKEN = "MuseumTitleTaken";
    static final String CODE_PAINTING_TITLE_TAKEN = "PaintingTitleTaken";
    static final String CODE_USER_FORBIDDEN = "UserUpdateForbidden";
}
