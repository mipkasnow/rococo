package mipkasnow.dev.rococo.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import mipkasnow.dev.rococo.api.AuthApi;
import mipkasnow.dev.rococo.api.RestClient;
import mipkasnow.dev.rococo.api.core.ThreadSafeCookieStore;
import mipkasnow.dev.rococo.utils.OAuthUtils;
import okhttp3.ResponseBody;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public final class AuthApiClient extends RestClient {

    private static final boolean HTTP_LOGGING = false;
    private static final String CLIENT_ID = "client";
    private static final String RESPONSE_TYPE = "code";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String SCOPE = "openid profile";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final Pattern CSRF_INPUT = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"");
    private static final Pattern STATE_INPUT = Pattern.compile("name=\"state\" value=\"([^\"]+)\"");

    private final AuthApi authApi;
    private final String redirectUri;

    public AuthApiClient() {
        super(CFG.authUrl(), false, HTTP_LOGGING);
        this.authApi = create(AuthApi.class);
        this.redirectUri = CFG.frontUrl() + "authorized";
    }

    @Nonnull
    @Step("Регистрация пользователя {username}")
    public Response<Void> register(String username, String password) {
        try {
            String csrf = csrf(requireNonNull(authApi.requestRegisterForm().execute().body()));
            return authApi.register(username, password, password, csrf).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Nonnull
    @Step("Логин пользователя {username}")
    public Response<Void> login(String username, String password) {
        try {
            String csrf = csrf(requireNonNull(authApi.requestLoginForm().execute().body()));
            return authApi.login(username, password, csrf).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Nonnull
    @Step("OAuth логин пользователя {username}")
    public String doLogin(String username, String password) {
        ThreadSafeCookieStore.INSTANCE.removeAll();
        String codeVerifier = OAuthUtils.generateCodeVerifier();
        String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        try {
            authApi.authorize(
                    RESPONSE_TYPE,
                    CLIENT_ID,
                    SCOPE,
                    redirectUri,
                    codeChallenge,
                    CODE_CHALLENGE_METHOD
            ).execute();

            String csrf = csrf(requireNonNull(authApi.requestLoginForm().execute().body()));
            Response<Void> loggedIn = authApi.login(username, password, csrf).execute();
            String state;
            try (ResponseBody consentPage = requireNonNull(authApi.follow(location(loggedIn)).execute().body())) {
                state = input(consentPage.string(), STATE_INPUT);
            }

            Response<Void> consented = authApi.consent(CLIENT_ID, state, "profile").execute();
            String code = code(location(consented));

            Response<JsonNode> tokenResponse = authApi.token(
                    CLIENT_ID,
                    redirectUri,
                    GRANT_TYPE,
                    code,
                    codeVerifier
            ).execute();
            JsonNode token = requireNonNull(tokenResponse.body());
            JsonNode idToken = token.get("id_token");
            if (idToken == null || idToken.asText().isBlank()) {
                throw new AssertionError("id_token is missing in token response");
            }
            return idToken.asText();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Nonnull
    private static String csrf(ResponseBody body) throws IOException {
        try (body) {
            return input(body.string(), CSRF_INPUT);
        }
    }

    @Nonnull
    private static String input(String html, Pattern pattern) {
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            throw new IllegalStateException("Input matching " + pattern + " was not found");
        }
        return matcher.group(1);
    }

    @Nonnull
    private static String location(Response<?> response) {
        String location = response.headers().get("Location");
        if (location == null || location.isBlank()) {
            throw new IllegalStateException("Location header is missing, status " + response.code());
        }
        return location;
    }

    @Nonnull
    private static String code(String location) {
        String query = URI.create(location).getRawQuery();
        if (query == null) {
            throw new IllegalStateException("Authorization code is missing in " + location);
        }
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2 && "code".equals(parts[0])) {
                return URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }
        throw new IllegalStateException("Authorization code is missing in " + location);
    }
}
