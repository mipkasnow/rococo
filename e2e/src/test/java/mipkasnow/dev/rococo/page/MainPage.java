package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.component.Header;
import mipkasnow.dev.rococo.page.component.ProfileModal;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;
import static mipkasnow.dev.rococo.page.Elements.byText;

public final class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl();

    private final Header header = new Header();
    private final SelenideElement
            slogan = byText("Ваши любимые картины и художники всегда рядом"),
            paintingsCard = byCss("img[alt='Ссылка на картины']"),
            artistsCard = byCss("img[alt='Ссылка на художников']"),
            museumsCard = byCss("img[alt='Ссылка на музеи']");

    @Override
    @Nonnull
    @Step("Проверить, что главная страница загружена")
    public MainPage checkThatPageLoaded() {
        header.checkThatLoaded();
        slogan.shouldBe(visible);
        paintingsCard.shouldBe(visible);
        artistsCard.shouldBe(visible);
        museumsCard.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Открыть страницу входа")
    public LoginPage openLoginPage() {
        return header.openLoginPage().checkThatPageLoaded();
    }

    @Nonnull
    @Step("Проверить, что пользователь вошёл")
    public MainPage checkThatUserLoggedIn() {
        header.checkThatUserLoggedIn();
        return this;
    }

    @Nonnull
    @Step("Открыть профиль")
    public ProfileModal openProfile() {
        return header.openProfile();
    }

    @Nonnull
    @Step("Сравнить аватар в шапке с {expected}")
    public MainPage checkAvatar(String expected) {
        header.checkAvatar(expected);
        return this;
    }

    @Nonnull
    @Step("Проверить, что аватар отображается")
    public MainPage checkAvatarVisible() {
        header.checkAvatarVisible();
        return this;
    }

    @Nonnull
    public String avatarSrc() {
        return header.avatarSrc();
    }

    @Nonnull
    @Step("Проверить, что аватар изменился")
    public MainPage checkAvatarChanged(String previousSrc) {
        header.checkAvatarChanged(previousSrc);
        return this;
    }
}
