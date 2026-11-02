package mipkasnow.dev.rococo.test.web;

import io.qameta.allure.Feature;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.meta.WebTest;
import mipkasnow.dev.rococo.model.user.TestUser;
import mipkasnow.dev.rococo.page.MainPage;
import mipkasnow.dev.rococo.page.message.FieldMsg;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;

@WebTest
@Feature("Профиль")
public class ProfileTest {

    private static final String FIRSTNAME = "Павел";
    private static final String LASTNAME = "Тестов";
    private static final String AVATAR = "images/avatar/avatar1.jpg";
    private static final String ANOTHER_AVATAR = "images/avatar/avatar2.jpg";
    private static final String AVATAR_ICON = "screenshots/avatar/avatar1-icon.png";
    private static final String AVATAR_MODAL = "screenshots/avatar/avatar1-modal.png";
    private static final String ANOTHER_AVATAR_ICON = "screenshots/avatar/avatar2-icon.png";
    private static final String ANOTHER_AVATAR_MODAL = "screenshots/avatar/avatar2-modal.png";

    @Test
    @ApiLogin
    @DisplayName("У нового пользователя в профиле пустые имя и фамилия и нет аватара")
    void emptyProfileShouldShowUsernameWithoutAvatar(TestUser user) {
        new MainPage()
                .openProfile()
                .checkUsername(user.username())
                .checkNamesEmpty()
                .checkNoAvatar();
    }

    @Test
    @ApiLogin
    @DisplayName("Профиль сохраняет имя, фамилию и аватар")
    void profileShouldSaveNameAndAvatar(TestUser user) {
        new MainPage()
                .openProfile()
                .fillProfile(FIRSTNAME, LASTNAME)
                .uploadAvatar(AVATAR)
                .save()
                .checkAvatarVisible()
                .checkAvatar(AVATAR_ICON)
                .openProfile()
                .checkUsername(user.username())
                .checkNames(FIRSTNAME, LASTNAME)
                .checkAvatarVisible()
                .checkAvatar(AVATAR_MODAL);
    }

    @Test
    @ApiLogin
    @DisplayName("Новый аватар заменяет предыдущий, имя и фамилия остаются")
    void newAvatarShouldReplacePreviousOne() {
        MainPage mainPage = new MainPage()
                .openProfile()
                .fillProfile(FIRSTNAME, LASTNAME)
                .uploadAvatar(AVATAR)
                .save()
                .checkAvatarVisible();
        String previousAvatar = mainPage.avatarSrc();

        mainPage.openProfile()
                .uploadAvatar(ANOTHER_AVATAR)
                .save()
                .checkAvatarChanged(previousAvatar)
                .checkAvatar(ANOTHER_AVATAR_ICON)
                .openProfile()
                .checkNames(FIRSTNAME, LASTNAME)
                .checkAvatarVisible()
                .checkAvatar(ANOTHER_AVATAR_MODAL);
    }

    @Test
    @ApiLogin
    @DisplayName("Имя длиннее 255 символов не сохраняется")
    void tooLongFirstnameShouldBeRejected() {
        new MainPage()
                .openProfile()
                .fillProfile(randomString(256), LASTNAME)
                .submit()
                .checkFieldNotHaveError(FieldMsg.LASTNAME_TOO_LONG)
                .checkFieldError(FieldMsg.FIRSTNAME_TOO_LONG);
    }

    @Test
    @ApiLogin
    @DisplayName("Фамилия длиннее 255 символов не сохраняется")
    void tooLongLastnameShouldBeRejected() {
        new MainPage()
                .openProfile()
                .fillProfile(FIRSTNAME, randomString(256))
                .submit()
                .checkFieldNotHaveError(FieldMsg.FIRSTNAME_TOO_LONG)
                .checkFieldError(FieldMsg.LASTNAME_TOO_LONG);
    }

    @Test
    @ApiLogin
    @DisplayName("Слишком длинные имя и фамилия показывают обе ошибки")
    void tooLongFirstnameAndLastnameShouldBeRejected() {
        new MainPage()
                .openProfile()
                .fillProfile(randomString(256), randomString(256))
                .submit()
                .checkFieldError(FieldMsg.FIRSTNAME_TOO_LONG)
                .checkFieldError(FieldMsg.LASTNAME_TOO_LONG);
    }
}
