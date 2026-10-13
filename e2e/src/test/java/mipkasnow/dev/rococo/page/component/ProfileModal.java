package mipkasnow.dev.rococo.page.component;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.MainPage;
import mipkasnow.dev.rococo.page.message.SuccessMsg;
import mipkasnow.dev.rococo.utils.ScreenDiff;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;

@ParametersAreNonnullByDefault
public final class ProfileModal extends BaseComponent<ProfileModal> {

    private final SelenideElement title = self.$("header");
    private final SelenideElement username = self.$("h4");
    private final SelenideElement firstnameInput = self.$("input[name=firstname]");
    private final SelenideElement lastnameInput = self.$("input[name=surname]");
    private final SelenideElement avatarInput = self.$("input[type=file]");
    private final SelenideElement avatar = self.$("[data-testid=avatar]");
    private final SelenideElement avatarImage = self.$("[data-testid=avatar] img.avatar-image");
    private final SelenideElement avatarInitials = self.$("[data-testid=avatar] svg.avatar-initials");
    private final SelenideElement logoutButton = self.$(Selectors.byText("Выйти"));
    private final SelenideElement closeButton = self.$(Selectors.byText("Закрыть"));
    private final SelenideElement submitButton = self.$("button[type=submit]");

    public ProfileModal() {
        super(byCss("[role=dialog]"));
    }

    @Override
    @Nonnull
    @Step("Проверить, что профиль открыт")
    public ProfileModal checkThatLoaded() {
        self.shouldBe(visible);
        title.shouldHave(exactText("Профиль"));
        username.shouldBe(visible);
        firstnameInput.shouldBe(visible);
        lastnameInput.shouldBe(visible);
        avatarInput.should(exist);
        logoutButton.shouldBe(visible);
        closeButton.shouldBe(visible);
        submitButton.shouldHave(exactText("Обновить профиль"));
        return this;
    }

    @Nonnull
    @Step("Проверить логин @{username}")
    public ProfileModal checkUsername(String username) {
        this.username.shouldHave(exactText("@" + username));
        return this;
    }

    @Nonnull
    @Step("Проверить, что имя и фамилия пустые")
    public ProfileModal checkNamesEmpty() {
        firstnameInput.shouldBe(empty);
        lastnameInput.shouldBe(empty);
        return this;
    }

    @Nonnull
    @Step("Проверить имя и фамилию: {firstname} {lastname}")
    public ProfileModal checkNames(String firstname, String lastname) {
        firstnameInput.shouldHave(value(firstname));
        lastnameInput.shouldHave(value(lastname));
        return this;
    }

    @Nonnull
    @Step("Проверить, что аватар не задан")
    public ProfileModal checkNoAvatar() {
        avatarInitials.shouldBe(visible);
        avatarImage.should(not(exist));
        return this;
    }

    @Nonnull
    @Step("Сравнить аватар в профиле с {expected}")
    public ProfileModal checkAvatar(String expected) {
        ScreenDiff.check(avatar, expected);
        return this;
    }

    @Nonnull
    @Step("Проверить, что аватар отображается")
    public ProfileModal checkAvatarVisible() {
        avatarImage.shouldBe(visible).shouldHave(attributeMatching("src", "data:image/.+"));
        avatarInitials.should(not(exist));
        return this;
    }

    @Nonnull
    @Step("Заполнить профиль: {firstname} {lastname}")
    public ProfileModal fillProfile(String firstname, String lastname) {
        firstnameInput.setValue(firstname);
        lastnameInput.setValue(lastname);
        return this;
    }

    @Nonnull
    @Step("Загрузить аватар {classpath}")
    public ProfileModal uploadAvatar(String classpath) {
        avatarInput.uploadFromClasspath(classpath);
        return this;
    }

    @Nonnull
    @Step("Сохранить профиль")
    public MainPage save() {
        submitButton.click();
        self.should(disappear);
        return new MainPage().checkToastMsg(SuccessMsg.PROFILE_UPDATED);
    }

    @Nonnull
    @Step("Отправить невалидный профиль")
    public ProfileModal submit() {
        submitButton.click();
        self.shouldBe(visible);
        return this;
    }
}
