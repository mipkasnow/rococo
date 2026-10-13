package mipkasnow.dev.rococo.page.component;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.ArtistDetailPage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;

@ParametersAreNonnullByDefault
public final class NewPaintingModal extends BaseComponent<NewPaintingModal> {

    private final SelenideElement titleInput = self.$("input[name=title]");
    private final SelenideElement descriptionInput = self.$("textarea[name=description]");
    private final SelenideElement contentInput = self.$("input[name=content]");
    private final SelenideElement authorSelect = self.$("select[name=authorId]");
    private final SelenideElement museumSelect = self.$("select[name=museumId]");
    private final SelenideElement submitButton = self.$("button[type=submit]");

    private String title;

    public NewPaintingModal() {
        super(byCss("[role=dialog]"));
    }

    @Override
    @Nonnull
    @Step("Проверить, что форма новой картины художника открыта")
    public NewPaintingModal checkThatLoaded() {
        self.shouldBe(visible);
        titleInput.shouldBe(visible).shouldBe(empty);
        descriptionInput.shouldBe(visible).shouldBe(empty);
        contentInput.should(exist);
        authorSelect.should(not(exist));
        museumSelect.shouldBe(visible);
        submitButton.shouldHave(exactText("Добавить"));
        return this;
    }

    @Nonnull
    @Step("Заполнить картину: {title}")
    public NewPaintingModal fill(String title, String description) {
        this.title = title;
        titleInput.setValue(title);
        descriptionInput.setValue(description);
        return this;
    }

    @Nonnull
    @Step("Загрузить изображение картины {classpath}")
    public NewPaintingModal uploadContent(String classpath) {
        contentInput.uploadFromClasspath(classpath);
        return this;
    }

    @Nonnull
    @Step("Выбрать музей {title}")
    public NewPaintingModal selectMuseum(String title) {
        museumSelect.shouldBe(visible);
        for (int attempt = 0; attempt < 40 && !museumSelect.$$("option").findBy(exactText(title)).exists(); attempt++) {
            Selenide.executeJavaScript("arguments[0].scrollTop = arguments[0].scrollHeight", museumSelect);
            Selenide.sleep(400);
        }
        museumSelect.selectOption(title);
        return this;
    }

    @Nonnull
    @Step("Добавить картину")
    public ArtistDetailPage add() {
        submitButton.click();
        self.should(disappear);
        return new ArtistDetailPage().checkPaintingAdded(title);
    }
}
