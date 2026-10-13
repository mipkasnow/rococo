package mipkasnow.dev.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.LoginPage;
import mipkasnow.dev.rococo.utils.ScreenDiff;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;
import static mipkasnow.dev.rococo.page.Elements.byTestId;
import static mipkasnow.dev.rococo.page.Elements.byText;

public final class Header extends BaseComponent<Header> {

    private final SelenideElement
            logo = byCss("h1 a[href='/']"),
            paintingsLink = byCss("nav.hidden a[href='/painting']"),
            artistsLink = byCss("nav.hidden a[href='/artist']"),
            museumsLink = byCss("nav.hidden a[href='/museum']"),
            loginButton = byText("Войти"),
            profileButton = byCss("[data-testid='app-bar'] button.variant-filled-surface"),
            avatar = self.$("[data-testid=avatar]"),
            avatarImage = self.$("[data-testid=avatar] img.avatar-image");

    public Header() {
        super(byTestId("app-bar"));
    }

    @Override
    @Nonnull
    @Step("Проверить, что шапка загружена")
    public Header checkThatLoaded() {
        self.shouldBe(visible);
        logo.shouldBe(visible);
        paintingsLink.shouldBe(visible);
        artistsLink.shouldBe(visible);
        museumsLink.shouldBe(visible);
        loginButton.should(or("shown or absent", visible, not(exist)));
        profileButton.should(or("shown or absent", visible, not(exist)));
        return this;
    }

    @Nonnull
    @Step("Открыть страницу входа")
    public LoginPage openLoginPage() {
        loginButton.click();
        return new LoginPage();
    }

    @Nonnull
    @Step("Проверить, что пользователь вошёл")
    public Header checkThatUserLoggedIn() {
        profileButton.shouldBe(visible);
        loginButton.should(not(exist));
        return this;
    }

    @Nonnull
    @Step("Открыть профиль")
    public ProfileModal openProfile() {
        profileButton.click();
        return new ProfileModal().checkThatLoaded();
    }

    @Nonnull
    @Step("Сравнить аватар в шапке с {expected}")
    public Header checkAvatar(String expected) {
        ScreenDiff.check(avatar, expected);
        return this;
    }

    @Nonnull
    @Step("Проверить, что аватар отображается")
    public Header checkAvatarVisible() {
        avatarImage.shouldBe(visible).shouldHave(attributeMatching("src", "data:image/.+"));
        return this;
    }

    @Nonnull
    @Step("Запомнить адрес аватара")
    public String avatarSrc() {
        return Objects.requireNonNull(avatarImage.shouldBe(visible).getAttribute("src"));
    }

    @Nonnull
    @Step("Проверить, что аватар изменился")
    public Header checkAvatarChanged(String previousSrc) {
        avatarImage.shouldBe(visible)
                .shouldNotHave(attribute("src", previousSrc))
                .shouldHave(attributeMatching("src", "data:image/.+"));
        return this;
    }
}
