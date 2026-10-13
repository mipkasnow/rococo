package mipkasnow.dev.rococo.page.message;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;

import static mipkasnow.dev.rococo.page.Elements.byCss;

public enum ErrorMsg implements Message {

    ARTIST_NAME_TAKEN("Художник с таким именем уже существует"),
    MUSEUM_TITLE_TAKEN("Музей с таким названием уже существует"),
    PAINTING_TITLE_TAKEN("Картина с таким названием уже существует"),
    INVALID_IMAGE("Некорректное изображение");

    private static final String SELECTOR = "[data-testid=toast].variant-filled-error .text-base";

    private final String text;

    ErrorMsg(String text) {
        this.text = text;
    }

    @Override
    @Nonnull
    public String text() {
        return text;
    }

    @Override
    @Nonnull
    public SelenideElement element() {
        return byCss(SELECTOR);
    }

    @Override
    public String toString() {
        return text;
    }
}
