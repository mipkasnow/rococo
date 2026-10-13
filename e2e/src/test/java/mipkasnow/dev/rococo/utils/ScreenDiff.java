package mipkasnow.dev.rococo.utils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.experimental.UtilityClass;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static com.codeborne.selenide.Condition.visible;
import static org.junit.jupiter.api.Assertions.fail;

@UtilityClass
public class ScreenDiff {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String OVERRIDE = "screenshot.override";
    private static final double ALLOWED_DIFF_RATIO = 0.05;

    @Step("Сравнить скриншот с {expected}")
    public static void check(SelenideElement element, String expected) {
        BufferedImage actual = capture(element);
        if (Boolean.getBoolean(OVERRIDE)) {
            write(expected, actual);
            return;
        }
        BufferedImage expectedImage = read(expected);
        int pixels = Math.max(expectedImage.getWidth(), actual.getWidth())
                * Math.max(expectedImage.getHeight(), actual.getHeight());
        int allowed = (int) (pixels * ALLOWED_DIFF_RATIO);
        ImageDiff diff = new ImageDiffer()
                .makeDiff(expectedImage, actual)
                .withDiffSizeTrigger(allowed);
        if (diff.hasDiff()) {
            attach(expectedImage, actual, diff.getMarkedImage());
            fail(String.format(
                    "Скриншот не совпал с %s: отличий %.2f%% при допуске %.0f%%",
                    expected,
                    diff.getDiffSize() * 100.0 / pixels,
                    ALLOWED_DIFF_RATIO * 100
            ));
        }
    }

    @Nonnull
    private static BufferedImage capture(SelenideElement element) {
        SelenideElement target = element.shouldBe(visible);
        float devicePixelRatio = devicePixelRatio();
        return new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .shootingStrategy(ShootingStrategies.scaling(devicePixelRatio))
                .takeScreenshot(WebDriverRunner.getWebDriver(), target)
                .getImage();
    }

    private static float devicePixelRatio() {
        Number ratio = Selenide.executeJavaScript("return window.devicePixelRatio");
        return ratio == null ? 1f : ratio.floatValue();
    }

    @Nonnull
    private static BufferedImage read(String expected) {
        try (InputStream input = ScreenDiff.class.getClassLoader().getResourceAsStream(expected)) {
            if (input == null) {
                throw new IllegalStateException("Эталон не найден: " + expected + ". Запусти с -Dscreenshot.override=true");
            }
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                throw new IllegalStateException("Эталон не прочитан: " + expected);
            }
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException("Эталон не прочитан: " + expected, e);
        }
    }

    private static void write(String expected, BufferedImage actual) {
        Path file = resourcesDir().resolve(expected);
        try {
            Files.createDirectories(file.getParent());
            ImageIO.write(actual, "png", file.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException("Эталон не записан: " + file, e);
        }
    }

    @Nonnull
    private static Path resourcesDir() {
        Path module = Path.of("src/test/resources");
        if (Files.isDirectory(module)) {
            return module;
        }
        Path nested = Path.of("e2e/src/test/resources");
        if (Files.isDirectory(nested)) {
            return nested;
        }
        throw new IllegalStateException("Каталог src/test/resources не найден");
    }

    private static void attach(BufferedImage expected, BufferedImage actual, BufferedImage diff) {
        try {
            Allure.addAttachment(
                    "Screenshot diff",
                    "application/vnd.allure.image.diff",
                    MAPPER.writeValueAsString(new AllureImageDiff(dataUrl(expected), dataUrl(actual), dataUrl(diff)))
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Nonnull
    private static String dataUrl(BufferedImage image) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(png(image));
    }

    @Nonnull
    private static byte[] png(BufferedImage image) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record AllureImageDiff(String expected, String actual, String diff) {
    }
}
