package mipkasnow.dev.rococo.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.ByteArrayInputStream;

public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    static {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }
        String browser = System.getenv("SELENOID_BROWSER");
        if (browser == null || browser.isEmpty()) {
            browser = "chrome";
        }

        Configuration.browser = browser;
        Configuration.timeout = 8000;
        Configuration.pageLoadStrategy = "eager";

        if ("docker".equals(System.getProperty("test.env"))) {
            Configuration.remote = "http://selenoid:4444/wd/hub";
            if ("chrome".equals(browser)) {
                Configuration.browserVersion = "127.0";
                Configuration.browserCapabilities = new ChromeOptions().addArguments("--no-sandbox");
            } else if ("firefox".equals(browser)) {
                Configuration.browserVersion = "125.0";
                Configuration.browserCapabilities = new FirefoxOptions();
            }
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Allure.addAttachment("Browser", "text/plain", Configuration.browser + " " + Configuration.browserVersion);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private static void doScreenshot() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail",
                    new ByteArrayInputStream(
                            ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
    }
}
