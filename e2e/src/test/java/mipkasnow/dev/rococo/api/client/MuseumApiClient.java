package mipkasnow.dev.rococo.api.client;

import io.qameta.allure.Step;
import mipkasnow.dev.rococo.api.MuseumApi;
import mipkasnow.dev.rococo.api.RestClient;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.model.museum.MuseumUpdateRequest;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public final class MuseumApiClient extends RestClient {

    private final MuseumApi museumApi;

    public MuseumApiClient() {
        super(CFG.gatewayUrl());
        this.museumApi = create(MuseumApi.class);
    }

    @Nonnull
    public PageResponse<MuseumResponse> findAll() {
        return findAll(ApiLoginExtension.optionalToken(), null, null, null);
    }

    @Nonnull
    public PageResponse<MuseumResponse> findAll(@Nullable Integer page,
                                                @Nullable Integer size,
                                                @Nullable String title) {
        return findAll(ApiLoginExtension.optionalToken(), page, size, title);
    }

    @Nonnull
    public MuseumResponse findById(UUID id) {
        return findById(ApiLoginExtension.optionalToken(), id);
    }

    @Nonnull
    public MuseumResponse create(MuseumCreateRequest museum) {
        return create(ApiLoginExtension.token(), museum);
    }

    @Nonnull
    public MuseumResponse update(MuseumUpdateRequest museum) {
        return update(ApiLoginExtension.token(), museum);
    }

    @Nonnull
    @Step("GET /api/museum")
    public PageResponse<MuseumResponse> findAll(@Nullable String token,
                                                @Nullable Integer page,
                                                @Nullable Integer size,
                                                @Nullable String title) {
        return ApiCalls.body(museumApi.findAll(ApiCalls.optionalBearer(token), page, size, title));
    }

    @Nonnull
    @Step("GET /api/museum/{id}")
    public MuseumResponse findById(@Nullable String token, UUID id) {
        return ApiCalls.body(museumApi.findById(ApiCalls.optionalBearer(token), id));
    }

    @Nonnull
    @Step("POST /api/museum")
    public MuseumResponse create(String token, MuseumCreateRequest museum) {
        return ApiCalls.body(museumApi.create(ApiCalls.bearer(token), museum));
    }

    @Nonnull
    @Step("PATCH /api/museum")
    public MuseumResponse update(String token, MuseumUpdateRequest museum) {
        return ApiCalls.body(museumApi.update(ApiCalls.bearer(token), museum));
    }

    @Nonnull
    @Step("POST /api/museum")
    public Response<MuseumResponse> createResponse(@Nullable String token, MuseumCreateRequest museum) {
        return ApiCalls.response(museumApi.create(ApiCalls.optionalBearer(token), museum));
    }

    @Nonnull
    @Step("PATCH /api/museum")
    public Response<MuseumResponse> updateResponse(@Nullable String token, MuseumUpdateRequest museum) {
        return ApiCalls.response(museumApi.update(ApiCalls.optionalBearer(token), museum));
    }

    @Nonnull
    @Step("GET /api/museum/{id}")
    public Response<MuseumResponse> findByIdResponse(@Nullable String token, UUID id) {
        return ApiCalls.response(museumApi.findById(ApiCalls.optionalBearer(token), id));
    }
}
