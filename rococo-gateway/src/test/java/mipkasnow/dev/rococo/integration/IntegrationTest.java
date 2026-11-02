package mipkasnow.dev.rococo.integration;

import mipkasnow.dev.rococo.integration.support.IntegrationInfrastructure;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Tag("integrationTests")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
@Import(IntegrationInfrastructure.class)
public @interface IntegrationTest {
}
