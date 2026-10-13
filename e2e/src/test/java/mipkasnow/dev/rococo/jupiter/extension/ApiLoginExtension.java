package mipkasnow.dev.rococo.jupiter.extension;

import com.codeborne.selenide.Selenide;
import mipkasnow.dev.rococo.api.client.AuthApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.meta.WebTest;
import mipkasnow.dev.rococo.model.user.TestUser;
import mipkasnow.dev.rococo.page.MainPage;
import mipkasnow.dev.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class ApiLoginExtension implements BeforeAllCallback, BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private static final String USER_KEY = "user";

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Override
    public void beforeAll(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestClass(), ApiLogin.class)
                .ifPresent(apiLogin -> storedUser(context, apiLogin));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        TestUser user = resolveUser(context);
        if (user != null && AnnotationSupport.isAnnotated(context.getRequiredTestClass(), WebTest.class)) {
            openCabinet(user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(TestUser.class);
    }

    @Override
    @Nonnull
    public TestUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        TestUser user = storedUser(extensionContext);
        if (user == null) {
            throw new ParameterResolutionException("@ApiLogin is required to inject TestUser");
        }
        return user;
    }

    @Nonnull
    public static TestUser user() {
        return user(requiredContext());
    }

    @Nonnull
    public static TestUser user(ExtensionContext context) {
        TestUser user = storedUser(context);
        if (user == null) {
            throw new IllegalStateException("@ApiLogin is required to access the current test user");
        }
        return user;
    }

    @Nonnull
    public static String token() {
        return user().token();
    }

    @Nullable
    public static String optionalToken() {
        TestUser user = storedUser(requiredContext());
        return user == null ? null : user.token();
    }

    @Nullable
    private TestUser resolveUser(ExtensionContext context) {
        Optional<ApiLogin> methodLogin = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class);
        if (methodLogin.isPresent()) {
            TestUser user = registerAndLogin(methodLogin.get().user());
            context.getStore(NAMESPACE).put(USER_KEY, user);
            return user;
        }
        return AnnotationSupport.findAnnotation(context.getRequiredTestClass(), ApiLogin.class)
                .map(apiLogin -> storedUser(classContext(context), apiLogin))
                .orElse(null);
    }

    @Nonnull
    private TestUser storedUser(ExtensionContext context, ApiLogin apiLogin) {
        return context.getStore(NAMESPACE).getOrComputeIfAbsent(
                USER_KEY,
                key -> registerAndLogin(apiLogin.user()),
                TestUser.class
        );
    }

    @Nonnull
    private TestUser registerAndLogin(mipkasnow.dev.rococo.jupiter.annotation.TestUser credentials) {
        String username = username(credentials.username());
        String password = credentials.password().isBlank() ? RandomDataUtils.randomPassword() : credentials.password();
        Response<Void> registered = authApiClient.register(username, password);
        assertEquals(201, registered.code());
        String token = authApiClient.doLogin(username, password);
        return new TestUser(username, password, token);
    }

    @Nullable
    private static TestUser storedUser(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(USER_KEY, TestUser.class);
    }

    @Nonnull
    private static ExtensionContext classContext(ExtensionContext context) {
        return context.getParent().orElse(context);
    }

    @Nonnull
    private static ExtensionContext requiredContext() {
        ExtensionContext context = TestMethodContextExtension.context();
        if (context == null) {
            throw new IllegalStateException("Test method context is not available");
        }
        return context;
    }

    @Nonnull
    private static String username(String requested) {
        if (!requested.isBlank()) {
            return requested;
        }
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String base = RandomDataUtils.randomUsername();
        int maxBaseLength = 50 - suffix.length();
        if (base.length() > maxBaseLength) {
            base = base.substring(0, maxBaseLength);
        }
        return base + suffix;
    }

    private static void openCabinet(TestUser user) {
        Selenide.open(MainPage.URL);
        Selenide.localStorage().setItem("id_token", user.token());
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded()
                .checkThatUserLoggedIn();
    }
}
