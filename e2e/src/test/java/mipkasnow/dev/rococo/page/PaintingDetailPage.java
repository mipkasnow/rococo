package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.component.EditPaintingModal;
import mipkasnow.dev.rococo.page.component.Header;

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

public final class PaintingDetailPage extends BasePage<PaintingDetailPage> {

    private static final String UPDATED_TOAST = "[data-testid=toast].variant-filled-primary .text-base";

    private final Header header = new Header();
    private final SelenideElement
            cardTitle = byCss("article.card header.card-header"),
            artistName = byCss("article.card div.text-center"),
            description = byCss("article.card div.m-4:not(.grid)"),
            editButton = byTestId("edit-painting");

    @Nonnull
    public static String url(UUID id) {
        return CFG.frontUrl() + "painting/" + id;
    }

    @Override
    @Nonnull
    @Step("Проверить, что карточка картины загружена")
    public PaintingDetailPage checkThatPageLoaded() {
        header.checkThatLoaded();
        cardTitle.shouldBe(visible).shouldNotBe(empty);
        editButton.should(or("shown or absent", visible, not(exist)));
        return this;
    }

    @Nonnull
    @Step("Проверить название картины: {title}")
    public PaintingDetailPage checkTitle(String title) {
        cardTitle.shouldHave(exactText(title));
        return this;
    }

    @Nonnull
    @Step("Проверить автора картины: {name}")
    public PaintingDetailPage checkArtist(String name) {
        artistName.shouldHave(exactText(name));
        return this;
    }

    @Nonnull
    @Step("Проверить описание картины")
    public PaintingDetailPage checkDescription(String description) {
        this.description.shouldHave(exactText(description));
        return this;
    }

    @Nonnull
    @Step("Открыть редактирование картины")
    public EditPaintingModal openEdit() {
        editButton.click();
        return new EditPaintingModal().checkThatLoaded();
    }

    @Nonnull
    @Step("Проверить, что картина обновлена: {title}")
    public PaintingDetailPage checkUpdated(String title) {
        $$(UPDATED_TOAST).last().shouldBe(visible).shouldHave(exactText("Обновлена картина: " + title));
        cardTitle.shouldHave(exactText(title));
        return this;
    }
}
