package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.component.Header;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;
import static mipkasnow.dev.rococo.page.Elements.byTestId;

public final class MuseumDetailPage extends BasePage<MuseumDetailPage> {

    private final Header header = new Header();
    private final SelenideElement
            cardTitle = byCss("article.card header.card-header"),
            editButton = byTestId("edit-museum");

    @Nonnull
    public static String url(UUID id) {
        return CFG.frontUrl() + "museum/" + id;
    }

    @Override
    @Nonnull
    @Step("Проверить, что карточка музея загружена")
    public MuseumDetailPage checkThatPageLoaded() {
        header.checkThatLoaded();
        cardTitle.shouldBe(visible).shouldNotBe(empty);
        editButton.should(or("shown or absent", visible, not(exist)));
        return this;
    }
}
