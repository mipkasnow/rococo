package mipkasnow.dev.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.PaintingDetailPage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static mipkasnow.dev.rococo.page.Elements.byCss;

@ParametersAreNonnullByDefault
public final class EditPaintingModal extends BaseComponent<EditPaintingModal> {

    private final SelenideElement title = self.$("header");
    private final SelenideElement titleInput = self.$("input[name=title]");
    private final SelenideElement descriptionInput = self.$("textarea[name=description]");
    private final SelenideElement submitButton = self.$("button[type=submit]");

    private String updatedTitle;

    public EditPaintingModal() {
        super(byCss("[role=dialog]"));
    }

    @Override
    @Nonnull
    @Step("Проверить, что форма редактирования картины открыта")
    public EditPaintingModal checkThatLoaded() {
        self.shouldBe(visible);
        title.shouldHave(exactText("Редактировать картину"));
        titleInput.shouldBe(visible);
        descriptionInput.shouldBe(visible);
        submitButton.shouldHave(exactText("Сохранить"));
        return this;
    }

    @Nonnull
    @Step("Проверить название и описание в форме")
    public EditPaintingModal checkValues(String title, String description) {
        titleInput.shouldHave(value(title));
        descriptionInput.shouldHave(value(description));
        return this;
    }

    @Nonnull
    @Step("Изменить картину: {title}")
    public EditPaintingModal fill(String title, String description) {
        this.updatedTitle = title;
        titleInput.setValue(title);
        descriptionInput.setValue(description);
        return this;
    }

    @Nonnull
    @Step("Сохранить картину")
    public PaintingDetailPage save() {
        submitButton.click();
        self.should(disappear);
        return new PaintingDetailPage().checkUpdated(updatedTitle);
    }
}
