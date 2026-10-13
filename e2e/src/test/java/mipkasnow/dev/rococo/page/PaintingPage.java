package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.component.Header;
import mipkasnow.dev.rococo.page.component.SearchField;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;

public final class PaintingPage extends BasePage<PaintingPage> {

    public static final String URL = CFG.frontUrl() + "painting";

    private final Header header = new Header();
    private final SearchField search = new SearchField("Искать картины...");
    private final SelenideElement title = byCss("h2");

    @Override
    @Nonnull
    @Step("Проверить, что страница картин загружена")
    public PaintingPage checkThatPageLoaded() {
        header.checkThatLoaded();
        search.checkThatLoaded();
        title.shouldBe(visible).shouldHave(exactText("Картины"));
        return this;
    }
}
