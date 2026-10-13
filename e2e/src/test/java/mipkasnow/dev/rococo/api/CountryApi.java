package mipkasnow.dev.rococo.api;

import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.country.CountryDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface CountryApi {

    @GET("api/country")
    @Nonnull
    Call<PageResponse<CountryDto>> findAll(@Header("Authorization") @Nullable String bearerToken,
                                           @Query("page") @Nullable Integer page,
                                           @Query("size") @Nullable Integer size);
}
