package mipkasnow.dev.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;
import static mipkasnow.dev.rococo.page.Elements.byPlaceholder;

public final class SearchField extends BaseComponent<SearchField> {

    private final SelenideElement searchButton = byCss("img[alt='Иконка поиска']");

    public SearchField(String placeholder) {
        super(byPlaceholder(placeholder));
    }

    @Override
    @Nonnull
    @Step("Проверить, что строка поиска загружена")
    public SearchField checkThatLoaded() {
        self.shouldBe(visible);
        searchButton.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Найти: {query}")
    public SearchField search(String query) {
        self.setValue(query);
        searchButton.click();
        return this;
    }
}
