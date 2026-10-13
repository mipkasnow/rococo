package mipkasnow.dev.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.page.message.FieldMessage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {

    protected final SelenideElement self;

    protected BaseComponent(SelenideElement self) {
        this.self = self;
    }

    @Nonnull
    public SelenideElement getSelf() {
        return self;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public T checkThatLoaded() {
        self.shouldBe(visible);
        return (T) this;
    }

    @Nonnull
    @Step("Проверить ошибку поля: {message}")
    @SuppressWarnings("unchecked")
    public T checkFieldError(FieldMessage message) {
        message.element().shouldHave(exactText(message.text()));
        return (T) this;
    }

    @Nonnull
    @Step("Проверить, что у поля нет ошибки")
    @SuppressWarnings("unchecked")
    public T checkFieldNotHaveError(FieldMessage message) {
        message.element().shouldBe(empty);
        return (T) this;
    }
}
