package mipkasnow.dev.rococo.page.component;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.ArtistPage;
import mipkasnow.dev.rococo.page.message.ErrorMsg;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;

@ParametersAreNonnullByDefault
public final class ArtistModal extends BaseComponent<ArtistModal> {

    private final SelenideElement title = self.$("header");
    private final SelenideElement description = self.$("article");
    private final SelenideElement nameInput = self.$("input[name=name]");
    private final SelenideElement biographyInput = self.$("textarea[name=biography]");
    private final SelenideElement photoInput = self.$("input[name=photo]");
    private final SelenideElement closeButton = self.$(Selectors.byText("Закрыть"));
    private final SelenideElement submitButton = self.$("button[type=submit]");

    private String name;

    public ArtistModal() {
        super(byCss("[role=dialog]"));
    }

    @Override
    @Nonnull
    @Step("Проверить, что форма нового художника открыта")
    public ArtistModal checkThatLoaded() {
        self.shouldBe(visible);
        title.shouldHave(exactText("Новый художник"));
        description.shouldHave(exactText("Заполните форму, чтобы добавить нового художника"));
        nameInput.shouldBe(visible).shouldBe(empty);
        biographyInput.shouldBe(visible).shouldBe(empty);
        photoInput.should(exist);
        closeButton.shouldBe(visible);
        submitButton.shouldHave(exactText("Добавить"));
        return this;
    }

    @Nonnull
    @Step("Заполнить художника: {name}")
    public ArtistModal fill(String name, String biography) {
        this.name = name;
        nameInput.setValue(name);
        biographyInput.setValue(biography);
        return this;
    }

    @Nonnull
    @Step("Загрузить фото художника {classpath}")
    public ArtistModal uploadPhoto(String classpath) {
        photoInput.uploadFromClasspath(classpath);
        return this;
    }

    @Nonnull
    @Step("Добавить художника")
    public ArtistPage add() {
        submitButton.click();
        self.should(disappear);
        return new ArtistPage().checkAdded(name);
    }

    @Nonnull
    @Step("Отправить невалидного художника")
    public ArtistModal submit() {
        submitButton.click();
        self.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Отправить художника с занятым именем")
    public ArtistModal submitTakenName() {
        submitButton.click();
        self.shouldBe(visible);
        ErrorMsg.ARTIST_NAME_TAKEN.element()
                .shouldBe(visible)
                .shouldHave(exactText(ErrorMsg.ARTIST_NAME_TAKEN.text()));
        return this;
    }
}
