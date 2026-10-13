package mipkasnow.dev.rococo.page.message;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;

import static mipkasnow.dev.rococo.page.Elements.byCss;

public enum FieldMsg implements FieldMessage {

    FIRSTNAME_TOO_LONG("Имя не может быть длиннее 255 символов", "firstname"),
    LASTNAME_TOO_LONG("Фамилия не может быть длиннее 255 символов", "surname"),
    NAME_TOO_SHORT("Имя не может быть короче 3 символов", "name"),
    NAME_TOO_LONG("Имя не может быть длиннее 255 символов", "name"),
    BIOGRAPHY_TOO_SHORT("Биография не может быть короче 10 символов", "biography"),
    BIOGRAPHY_TOO_LONG("Биография не может быть длиннее 2000 символов", "biography"),
    TITLE_TOO_SHORT("Название не может быть короче 3 символов", "title"),
    TITLE_TOO_LONG("Название не может быть длиннее 255 символов", "title"),
    DESCRIPTION_TOO_SHORT("Описание не может быть короче 10 символов", "description"),
    DESCRIPTION_TOO_LONG("Описание не может быть длиннее 2000 символов", "description"),
    CITY_TOO_SHORT("Город не может быть короче 3 символов", "city"),
    CITY_TOO_LONG("Город не может быть длиннее 255 символов", "city"),
    COUNTRY_REQUIRED("Укажите страну нахождения музея", "countryId"),
    AUTHOR_REQUIRED("Укажите автора картины", "authorId"),
    PHOTO_TOO_BIG("Максимальный размер изображения 15 Mb", "photo");

    private final String text;
    private final String fieldName;

    FieldMsg(String text, String fieldName) {
        this.text = text;
        this.fieldName = fieldName;
    }

    @Override
    @Nonnull
    public String text() {
        return text;
    }

    @Override
    @Nonnull
    public SelenideElement element() {
        return byCss("[role=dialog] [name=" + fieldName + "]").parent().$("span.text-error-400");
    }

    @Override
    public String toString() {
        return text;
    }
}
