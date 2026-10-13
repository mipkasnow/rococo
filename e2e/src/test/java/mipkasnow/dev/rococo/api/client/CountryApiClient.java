package mipkasnow.dev.rococo.api.client;

import io.qameta.allure.Step;
import mipkasnow.dev.rococo.api.CountryApi;
import mipkasnow.dev.rococo.api.RestClient;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.country.CountryDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class CountryApiClient extends RestClient {

    private final CountryApi countryApi;

    public CountryApiClient() {
        super(CFG.gatewayUrl());
        this.countryApi = create(CountryApi.class);
    }

    @Nonnull
    public PageResponse<CountryDto> findAll() {
        return findAll(ApiLoginExtension.optionalToken(), null, null);
    }

    @Nonnull
    public PageResponse<CountryDto> findAll(@Nullable Integer page, @Nullable Integer size) {
        return findAll(ApiLoginExtension.optionalToken(), page, size);
    }

    @Nonnull
    @Step("GET /api/country")
    public PageResponse<CountryDto> findAll(@Nullable String token,
                                            @Nullable Integer page,
                                            @Nullable Integer size) {
        return ApiCalls.body(countryApi.findAll(ApiCalls.optionalBearer(token), page, size));
    }
}
