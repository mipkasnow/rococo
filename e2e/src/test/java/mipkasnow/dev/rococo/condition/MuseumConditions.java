package mipkasnow.dev.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.impl.CollectionSource;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class MuseumConditions {

    @Nonnull
    public static WebElementsCondition museums(@Nonnull MuseumResponse... expected) {
        return new WebElementsCondition() {
            @Override
            @NonNull
            public CheckResult check(@NonNull Driver driver, @NonNull List<WebElement> elements) {
                if (expected.length != elements.size()) {
                    return mismatch(String.format(
                            "Количество музеев не совпадает (ожидалось: %d, на странице: %d)",
                            expected.length,
                            elements.size()
                    ));
                }
                for (int i = 0; i < elements.size(); i++) {
                    List<WebElement> lines = elements.get(i).findElements(By.cssSelector("div.text-center"));
                    MuseumResponse museum = expected[i];
                    if (lines.size() < 2) {
                        return mismatch("В карточке музея нет названия или страны: " + museum.title());
                    }
                    String actualTitle = lines.getFirst().getText().trim();
                    if (!actualTitle.equals(museum.title())) {
                        return mismatch(String.format(
                                "Название музея не совпадает (ожидалось: %s, на странице: %s)",
                                museum.title(),
                                actualTitle
                        ));
                    }
                    String expectedPlace = museum.geo().city() + ", " + museum.geo().country().name();
                    String actualPlace = lines.get(1).getText().trim();
                    if (!actualPlace.equals(expectedPlace)) {
                        return mismatch(String.format(
                                "Страна музея не совпадает (ожидалось: %s, на странице: %s)",
                                expectedPlace,
                                actualPlace
                        ));
                    }
                }
                return accepted();
            }

            @Override
            public void fail(@NonNull CollectionSource collection,
                             @NonNull CheckResult lastCheckResult,
                             @Nullable Exception cause,
                             long timeoutMs) {
                String message = lastCheckResult.message();
                throw new MuseumMismatchException(message == null ? "Список музеев не совпал с ожиданием" : message);
            }

            @Override
            @NonNull
            public String toString() {
                return Arrays.stream(expected).map(MuseumResponse::title).toList().toString();
            }

            @Nonnull
            private CheckResult mismatch(String message) {
                return rejected(message, message);
            }
        };
    }

    public static class MuseumMismatchException extends AssertionError {

        public MuseumMismatchException(String message) {
            super(message);
        }
    }
}
