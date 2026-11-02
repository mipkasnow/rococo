package mipkasnow.dev.rococo.test.rest;

import io.qameta.allure.Feature;
import mipkasnow.dev.rococo.api.client.SessionApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.meta.RestTest;
import mipkasnow.dev.rococo.model.session.SessionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

@RestTest
@Feature("Сессия")
public class SessionRestTest {

    private SessionApiClient sessionApiClient;

    @Test
    @DisplayName("Битый токен сессии отклоняется")
    void brokenTokenShouldBeUnauthorized() {
        Response<SessionResponse> response = sessionApiClient.currentResponse("broken-token");
        RestAssert.error("битый токен", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }
}
