package mipkasnow.dev.rococo.api.client;

import io.qameta.allure.Step;
import mipkasnow.dev.rococo.api.UserApi;
import mipkasnow.dev.rococo.api.RestClient;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.user.UserResponse;
import mipkasnow.dev.rococo.model.user.UserUpdateRequest;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class UserApiClient extends RestClient {

    private final UserApi userApi;

    public UserApiClient() {
        super(CFG.gatewayUrl());
        this.userApi = create(UserApi.class);
    }

    @Nonnull
    public UserResponse current() {
        return current(ApiLoginExtension.token());
    }

    @Nonnull
    public UserResponse update(UserUpdateRequest user) {
        return update(ApiLoginExtension.token(), user);
    }

    @Nonnull
    public Response<UserResponse> updateResponse(UserUpdateRequest user) {
        return updateResponse(ApiLoginExtension.token(), user);
    }

    @Nonnull
    @Step("GET /api/user")
    public UserResponse current(String token) {
        return ApiCalls.body(userApi.current(ApiCalls.bearer(token)));
    }

    @Nonnull
    @Step("PATCH /api/user")
    public UserResponse update(String token, UserUpdateRequest user) {
        return ApiCalls.body(userApi.update(ApiCalls.bearer(token), user));
    }

    @Nonnull
    @Step("GET /api/user")
    public Response<UserResponse> currentResponse(@Nullable String token) {
        return ApiCalls.response(userApi.current(ApiCalls.optionalBearer(token)));
    }

    @Nonnull
    @Step("PATCH /api/user")
    public Response<UserResponse> updateResponse(@Nullable String token, UserUpdateRequest user) {
        return ApiCalls.response(userApi.update(ApiCalls.optionalBearer(token), user));
    }
}
