package mipkasnow.dev.rococo.page.message;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;

public interface FieldMessage {

    @Nonnull
    String text();

    @Nonnull
    SelenideElement element();
}
