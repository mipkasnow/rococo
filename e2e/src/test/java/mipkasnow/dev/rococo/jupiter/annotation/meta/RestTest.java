package mipkasnow.dev.rococo.jupiter.annotation.meta;

import io.qameta.allure.Epic;
import io.qameta.allure.junit5.AllureJunit5;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.jupiter.extension.CookieExtension;
import mipkasnow.dev.rococo.jupiter.extension.InjectDependenciesExtension;
import mipkasnow.dev.rococo.jupiter.extension.TestMethodContextExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Epic("API")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        TestMethodContextExtension.class,
        InjectDependenciesExtension.class,
        ApiLoginExtension.class,
        CookieExtension.class,
        AllureJunit5.class
})
public @interface RestTest {
}
