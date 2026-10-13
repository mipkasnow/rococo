package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.component.Header;
import mipkasnow.dev.rococo.page.component.NewPaintingModal;
import mipkasnow.dev.rococo.utils.ScreenDiff;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static mipkasnow.dev.rococo.page.Elements.byCss;
import static mipkasnow.dev.rococo.page.Elements.byTestId;
import static mipkasnow.dev.rococo.page.Elements.byText;

public final class ArtistDetailPage extends BasePage<ArtistDetailPage> {

    private final Header header = new Header();
    private static final String ADDED_TOAST = "[data-testid=toast].variant-filled-primary .text-base";

    private final SelenideElement
            cardTitle = byCss("article.card header.card-header"),
            biography = byCss("article.card p.col-span-2"),
            photo = byCss("article.card [data-testid=avatar]"),
            editButton = byTestId("edit-artist"),
            addPaintingButton = byText("Добавить картину");

    @Nonnull
    public static String url(UUID id) {
        return CFG.frontUrl() + "artist/" + id;
    }

    @Override
    @Nonnull
    @Step("Проверить, что карточка художника загружена")
    public ArtistDetailPage checkThatPageLoaded() {
        header.checkThatLoaded();
        cardTitle.shouldBe(visible).shouldNotBe(empty);
        editButton.should(or("shown or absent", visible, not(exist)));
        return this;
    }

    @Nonnull
    @Step("Проверить имя художника: {name}")
    public ArtistDetailPage checkName(String name) {
        cardTitle.shouldHave(exactText(name));
        return this;
    }

    @Nonnull
    @Step("Проверить биографию художника")
    public ArtistDetailPage checkBiography(String biography) {
        this.biography.shouldHave(exactText(biography));
        return this;
    }

    @Nonnull
    @Step("Сравнить фото на карточке художника с {expected}")
    public ArtistDetailPage checkPhoto(String expected) {
        ScreenDiff.check(photo, expected);
        return this;
    }

    @Nonnull
    @Step("Открыть форму новой картины")
    public NewPaintingModal openNewPainting() {
        addPaintingButton.click();
        return new NewPaintingModal().checkThatLoaded();
    }

    @Nonnull
    @Step("Проверить, что добавлена картина {title}")
    public ArtistDetailPage checkPaintingAdded(String title) {
        $$(ADDED_TOAST).last().shouldBe(visible).shouldHave(exactText("Добавлена картина: " + title));
        return this;
    }

    @Nonnull
    @Step("Проверить, что у художника есть картина {title}")
    public ArtistDetailPage checkPainting(String title) {
        paintingLink(title).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Открыть картину {title}")
    public PaintingDetailPage openPainting(String title) {
        paintingLink(title).click();
        return new PaintingDetailPage().checkThatPageLoaded();
    }

    @Nonnull
    private SelenideElement paintingLink(String title) {
        return $$("a[href^='/painting/']").findBy(exactText(title));
    }
}
