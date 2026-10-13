package mipkasnow.dev.rococo.api;

import mipkasnow.dev.rococo.model.session.SessionResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SessionApi {

    @GET("api/session")
    @Nonnull
    Call<SessionResponse> current(@Header("Authorization") @Nullable String bearerToken);
}
