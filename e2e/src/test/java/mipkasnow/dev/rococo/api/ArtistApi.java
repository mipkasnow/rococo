package mipkasnow.dev.rococo.api;

import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.model.artist.ArtistResponse;
import mipkasnow.dev.rococo.model.artist.ArtistUpdateRequest;
import okhttp3.RequestBody;
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
public interface ArtistApi {

    @GET("api/artist")
    @Nonnull
    Call<PageResponse<ArtistResponse>> findAll(@Header("Authorization") @Nullable String bearerToken,
                                               @Query("page") @Nullable Integer page,
                                               @Query("size") @Nullable Integer size,
                                               @Query("name") @Nullable String name);

    @GET("api/artist/{artistId}")
    @Nonnull
    Call<ArtistResponse> findById(@Header("Authorization") @Nullable String bearerToken,
                                  @Path("artistId") UUID artistId);

    @POST("api/artist")
    @Nonnull
    Call<ArtistResponse> create(@Header("Authorization") @Nullable String bearerToken,
                                @Body ArtistCreateRequest artist);

    @POST("api/artist")
    @Nonnull
    Call<ArtistResponse> createRaw(@Header("Authorization") @Nullable String bearerToken,
                                   @Body RequestBody body);

    @PATCH("api/artist")
    @Nonnull
    Call<ArtistResponse> update(@Header("Authorization") @Nullable String bearerToken,
                                @Body ArtistUpdateRequest artist);
}
