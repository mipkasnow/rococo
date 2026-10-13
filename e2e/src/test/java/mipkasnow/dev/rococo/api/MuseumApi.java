package mipkasnow.dev.rococo.api;

import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.model.museum.MuseumUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumApi {

    @GET("api/museum")
    @Nonnull
    Call<PageResponse<MuseumResponse>> findAll(@Header("Authorization") @Nullable String bearerToken,
                                               @Query("page") @Nullable Integer page,
                                               @Query("size") @Nullable Integer size,
                                               @Query("title") @Nullable String title);

    @GET("api/museum/{museumId}")
    @Nonnull
    Call<MuseumResponse> findById(@Header("Authorization") @Nullable String bearerToken,
                                  @Path("museumId") UUID museumId);

    @POST("api/museum")
    @Nonnull
    Call<MuseumResponse> create(@Header("Authorization") @Nullable String bearerToken,
                                @Body MuseumCreateRequest museum);

    @PATCH("api/museum")
    @Nonnull
    Call<MuseumResponse> update(@Header("Authorization") @Nullable String bearerToken,
                                @Body MuseumUpdateRequest museum);
}
