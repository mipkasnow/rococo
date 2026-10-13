package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;

public final class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement
            usernameInput = byCss("input[name='username']"),
            passwordInput = byCss("input[name='password']"),
            submitButton = byCss("button.form__submit"),
            registerLink = byCss("a.form__link"),
            errorContainer = byCss(".form__error.login__error");

    @Override
    @Nonnull
    @Step("Проверить, что страница входа загружена")
    public LoginPage checkThatPageLoaded() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        registerLink.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Заполнить форму входа: {username}")
    public LoginPage fillLoginPage(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        return this;
    }

    @Nonnull
    @Step("Отправить форму входа")
    public <T extends BasePage<?>> T submit(T expectedPage) {
        submitButton.click();
        return expectedPage;
    }

    @Nonnull
    @Step("Проверить ошибку входа: {error}")
    public LoginPage checkError(String error) {
        errorContainer.shouldHave(text(error));
        return this;
    }
}
