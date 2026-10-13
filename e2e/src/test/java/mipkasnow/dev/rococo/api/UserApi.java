package mipkasnow.dev.rococo.api;

import mipkasnow.dev.rococo.model.user.UserResponse;
import mipkasnow.dev.rococo.model.user.UserUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserApi {

    @GET("api/user")
    @Nonnull
    Call<UserResponse> current(@Header("Authorization") @Nullable String bearerToken);

    @PATCH("api/user")
    @Nonnull
    Call<UserResponse> update(@Header("Authorization") @Nullable String bearerToken,
                              @Body UserUpdateRequest user);
}
