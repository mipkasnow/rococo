package mipkasnow.dev.rococo.page.message;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;

import static mipkasnow.dev.rococo.page.Elements.byCss;

public enum SuccessMsg implements Message {

    PROFILE_UPDATED("Профиль обновлен");

    private static final String SELECTOR = "[data-testid=toast].variant-filled-primary .text-base";

    private final String text;

    SuccessMsg(String text) {
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
