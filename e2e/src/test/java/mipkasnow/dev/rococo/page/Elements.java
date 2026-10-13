package mipkasnow.dev.rococo.page;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.experimental.UtilityClass;

import static com.codeborne.selenide.Selenide.$;

@UtilityClass
public class Elements {

    public static SelenideElement byCss(String css) {
        return $(css);
    }

    public static SelenideElement byText(String text) {
        return $(Selectors.byText(text));
    }

    public static SelenideElement byPlaceholder(String placeholder) {
        return $(Selectors.byAttribute("placeholder", placeholder));
    }

    public static SelenideElement byTestId(String testId) {
        return $(Selectors.byAttribute("data-testid", testId));
    }
}
