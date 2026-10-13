package mipkasnow.dev.rococo.page;

import io.qameta.allure.Step;
import mipkasnow.dev.rococo.config.Config;
import mipkasnow.dev.rococo.page.message.Message;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    @Nonnull
    public abstract T checkThatPageLoaded();

    @Nonnull
    @Step("Проверить тост: {message}")
    @SuppressWarnings("unchecked")
    public T checkToastMsg(Message message) {
        message.element().shouldBe(visible).shouldHave(exactText(message.text()));
        return (T) this;
    }
}
