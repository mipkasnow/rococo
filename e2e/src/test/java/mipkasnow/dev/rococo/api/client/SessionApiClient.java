package mipkasnow.dev.rococo.api.client;

import io.qameta.allure.Step;
import mipkasnow.dev.rococo.api.SessionApi;
import mipkasnow.dev.rococo.api.RestClient;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.session.SessionResponse;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class SessionApiClient extends RestClient {

    private final SessionApi sessionApi;

    public SessionApiClient() {
        super(CFG.gatewayUrl());
        this.sessionApi = create(SessionApi.class);
    }

    @Nonnull
    public SessionResponse current() {
        return current(ApiLoginExtension.token());
    }

    @Nonnull
    @Step("GET /api/session")
    public SessionResponse current(@Nullable String token) {
        return ApiCalls.body(sessionApi.current(ApiCalls.optionalBearer(token)));
    }

    @Nonnull
    @Step("GET /api/session")
    public Response<SessionResponse> currentResponse(@Nullable String token) {
        return ApiCalls.response(sessionApi.current(ApiCalls.optionalBearer(token)));
    }
}
