package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.domProperty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;

public final class RegisterPage extends BasePage<RegisterPage> {

    public static final String URL = CFG.authUrl() + "register";

    private final SelenideElement
            usernameInput = byCss("#username"),
            passwordInput = byCss("#password"),
            passwordSubmitInput = byCss("#passwordSubmit"),
            submitButton = byCss("button.form__submit"),
            usernameError = byCss(".form__error.error__username"),
            passwordError = byCss(".form__error.error__password"),
            welcome = byCss(".form__subheader"),
            loginLink = byCss("a.form__submit");

    @Override
    @Nonnull
    @Step("Проверить, что страница регистрации загружена")
    public RegisterPage checkThatPageLoaded() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        passwordSubmitInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Заполнить форму регистрации: {username}")
    public RegisterPage fillRegistration(String username, String password, String passwordSubmit) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(passwordSubmit);
        return this;
    }

    @Nonnull
    @Step("Отправить форму регистрации")
    public RegisterPage submit() {
        submitButton.click();
        return this;
    }

    @Nonnull
    @Step("Проверить ошибку имени пользователя: {error}")
    public RegisterPage checkUsernameError(String error) {
        usernameError.shouldHave(exactText(error));
        return this;
    }

    @Nonnull
    @Step("Проверить ошибку пароля: {error}")
    public RegisterPage checkPasswordError(String error) {
        passwordError.shouldHave(exactText(error));
        return this;
    }

    @Nonnull
    @Step("Проверить, что пустое имя пользователя не даёт отправить форму")
    public RegisterPage checkUsernameRequired() {
        usernameInput.shouldHave(domProperty("validationMessage", "Заполните это поле."));
        return this;
    }

    @Nonnull
    @Step("Проверить, что регистрация прошла")
    public RegisterPage checkRegistered() {
        welcome.shouldHave(text("Добро пожаловать"));
        loginLink.shouldBe(visible).shouldHave(exactText("Войти в систему"));
        return this;
    }
}
