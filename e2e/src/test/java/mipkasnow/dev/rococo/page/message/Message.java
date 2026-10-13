package mipkasnow.dev.rococo.page.message;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;

public interface Message {

    @Nonnull
    String text();

    @Nonnull
    SelenideElement element();
}
