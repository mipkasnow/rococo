package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.condition.MuseumConditions;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.page.component.Header;
import mipkasnow.dev.rococo.page.component.SearchField;

import javax.annotation.Nonnull;
import java.util.List;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static mipkasnow.dev.rococo.page.Elements.byCss;

public final class MuseumPage extends BasePage<MuseumPage> {

    public static final String URL = CFG.frontUrl() + "museum";

    private final Header header = new Header();
    private final SearchField search = new SearchField("Искать музей...");
    private final SelenideElement title = byCss("h2");

    @Override
    @Nonnull
    @Step("Проверить, что страница музеев загружена")
    public MuseumPage checkThatPageLoaded() {
        header.checkThatLoaded();
        search.checkThatLoaded();
        title.shouldBe(visible).shouldHave(exactText("Музеи"));
        return this;
    }

    @Nonnull
    @Step("Проверить музеи на странице")
    public MuseumPage checkMuseums(List<MuseumResponse> museums) {
        $$("ul.grid > li").shouldHave(MuseumConditions.museums(museums.toArray(MuseumResponse[]::new)));
        return this;
    }

    @Nonnull
    @Step("Найти музей {query}")
    public MuseumPage search(String query) {
        search.search(query);
        return this;
    }

    @Nonnull
    @Step("Проверить, что найден музей {title}")
    public MuseumPage checkFoundTitle(String title) {
        $$("ul.grid > li div.text-center").findBy(exactText(title)).shouldBe(visible);
        return this;
    }
}
