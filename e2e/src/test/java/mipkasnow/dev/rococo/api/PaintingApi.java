package mipkasnow.dev.rococo.api;

import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.painting.PaintingCreateRequest;
import mipkasnow.dev.rococo.model.painting.PaintingResponse;
import mipkasnow.dev.rococo.model.painting.PaintingUpdateRequest;
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
public interface PaintingApi {

    @GET("api/painting")
    @Nonnull
    Call<PageResponse<PaintingResponse>> findAll(@Header("Authorization") @Nullable String bearerToken,
                                                 @Query("page") @Nullable Integer page,
                                                 @Query("size") @Nullable Integer size,
                                                 @Query("title") @Nullable String title);

    @GET("api/painting/author/{artistId}")
    @Nonnull
    Call<PageResponse<PaintingResponse>> findByArtist(@Header("Authorization") @Nullable String bearerToken,
                                                      @Path("artistId") UUID artistId,
                                                      @Query("page") @Nullable Integer page,
                                                      @Query("size") @Nullable Integer size);

    @GET("api/painting/{paintingId}")
    @Nonnull
    Call<PaintingResponse> findById(@Header("Authorization") @Nullable String bearerToken,
                                    @Path("paintingId") UUID paintingId);

    @POST("api/painting")
    @Nonnull
    Call<PaintingResponse> create(@Header("Authorization") @Nullable String bearerToken,
                                  @Body PaintingCreateRequest painting);

    @PATCH("api/painting")
    @Nonnull
    Call<PaintingResponse> update(@Header("Authorization") @Nullable String bearerToken,
                                  @Body PaintingUpdateRequest painting);
}
