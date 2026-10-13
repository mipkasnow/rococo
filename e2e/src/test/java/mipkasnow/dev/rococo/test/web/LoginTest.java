package mipkasnow.dev.rococo.test.web;

import com.codeborne.selenide.Selenide;
import mipkasnow.dev.rococo.api.client.AuthApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.meta.WebTest;
import mipkasnow.dev.rococo.page.LoginPage;
import mipkasnow.dev.rococo.page.MainPage;
import mipkasnow.dev.rococo.page.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomPassword;
import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomUsername;

@WebTest
public class LoginTest {

    private AuthApiClient authApi;

    @Test
    @DisplayName("После входа с верными данными открывается главная и виден профиль")
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        String username = randomUsername();
        String password = randomPassword();
        authApi.register(username, password);

        Selenide.open(MainPage.URL, MainPage.class)
                .openLoginPage()
                .fillLoginPage(username, password)
                .submit(new MainPage())
                .checkThatPageLoaded()
                .checkThatUserLoggedIn();
    }

    @Test
    @DisplayName("При неверном пароле остаёмся на странице входа и видим ошибку")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        Selenide.open(MainPage.URL, MainPage.class)
                .openLoginPage()
                .fillLoginPage("test", "bad")
                .submit(new LoginPage())
                .checkError("Неверные учетные данные пользователя");
    }

    @Test
    @DisplayName("Успешная регистрация показывает приветствие и ссылку на вход")
    void welcomeShouldBeDisplayedAfterSuccessfulRegistration() {
        String username = randomUsername();
        String password = randomPassword();

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .fillRegistration(username, password, password)
                .submit()
                .checkRegistered();
    }

    @Test
    @DisplayName("Пустое имя пользователя не даёт отправить форму регистрации")
    void emptyUsernameShouldBeRejectedByTheRegistrationForm() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .submit()
                .checkUsernameRequired();
    }

    @Test
    @DisplayName("Имя пользователя короче 3 символов не регистрируется")
    void tooShortUsernameShouldBeRejected() {
        String password = randomPassword();

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .fillRegistration("ab", password, password)
                .submit()
                .checkUsernameError("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    @DisplayName("Пароль короче 3 символов не регистрируется")
    void tooShortPasswordShouldBeRejected() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .fillRegistration(randomUsername(), "12", "12")
                .submit()
                .checkPasswordError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    @DisplayName("Пароль длиннее 12 символов не регистрируется")
    void tooLongPasswordShouldBeRejected() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .fillRegistration(randomUsername(), "1234567890123", "1234567890123")
                .submit()
                .checkPasswordError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    @DisplayName("Имя пользователя с пробелом не регистрируется")
    void usernameWithWhitespaceShouldBeRejected() {
        String password = randomPassword();

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .fillRegistration("ab c", password, password)
                .submit()
                .checkUsernameError("Username must not contain whitespaces");
    }

    @Test
    @DisplayName("Пароль с пробелом не регистрируется")
    void passwordWithWhitespaceShouldBeRejected() {
        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .fillRegistration(randomUsername(), "12 45", "12 45")
                .submit()
                .checkPasswordError("Password must not contain whitespaces");
    }

    @Test
    @DisplayName("Разные пароли не регистрируются")
    void differentPasswordsShouldBeRejected() {
        String password = randomPassword();
        String anotherPassword = password + "a";

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .fillRegistration(randomUsername(), password, anotherPassword)
                .submit()
                .checkPasswordError("Passwords should be equal");
    }

    @Test
    @DisplayName("Уже занятое имя пользователя не регистрируется")
    void existingUsernameShouldBeRejected() {
        String username = randomUsername();
        String password = randomPassword();
        authApi.register(username, password);

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .checkThatPageLoaded()
                .fillRegistration(username, password, password)
                .submit()
                .checkUsernameError("Username `" + username + "` already exists");
    }

}
