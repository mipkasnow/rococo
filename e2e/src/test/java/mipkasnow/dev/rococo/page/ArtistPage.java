package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.component.ArtistModal;
import mipkasnow.dev.rococo.page.component.Header;
import mipkasnow.dev.rococo.page.component.SearchField;
import mipkasnow.dev.rococo.utils.ScreenDiff;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static mipkasnow.dev.rococo.page.Elements.byCss;
import static mipkasnow.dev.rococo.page.Elements.byText;

public final class ArtistPage extends BasePage<ArtistPage> {

    public static final String URL = CFG.frontUrl() + "artist";

    private static final String ADDED_TOAST = "[data-testid=toast].variant-filled-primary .text-base";

    private final Header header = new Header();
    private final SearchField search = new SearchField("Искать художников...");
    private final SelenideElement title = byCss("h2");
    private final SelenideElement addButton = byText("Добавить художника");

    @Override
    @Nonnull
    @Step("Проверить, что страница художников загружена")
    public ArtistPage checkThatPageLoaded() {
        header.checkThatLoaded();
        search.checkThatLoaded();
        title.shouldBe(visible).shouldHave(exactText("Художники"));
        return this;
    }

    @Nonnull
    @Step("Открыть форму нового художника")
    public ArtistModal openNewArtist() {
        addButton.click();
        return new ArtistModal().checkThatLoaded();
    }

    @Nonnull
    @Step("Проверить, что добавлен художник {name}")
    public ArtistPage checkAdded(String name) {
        byCss(ADDED_TOAST).shouldBe(visible).shouldHave(exactText("Добавлен художник: " + name));
        return this;
    }

    @Nonnull
    @Step("Проверить, что в списке есть художник {name}")
    public ArtistPage checkArtist(String name) {
        artistLink(name).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Сравнить фото художника {name} в списке с {expected}")
    public ArtistPage checkPhoto(String name, String expected) {
        ScreenDiff.check(artistLink(name).$("[data-testid=avatar]"), expected);
        return this;
    }

    @Nonnull
    @Step("Открыть художника {name}")
    public ArtistDetailPage openArtist(String name) {
        artistLink(name).click();
        return new ArtistDetailPage().checkThatPageLoaded();
    }

    @Nonnull
    private SelenideElement artistLink(String name) {
        return $$("a[href^='/artist/']").findBy(exactText(name));
    }
}
