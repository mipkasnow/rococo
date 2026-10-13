package mipkasnow.dev.rococo.api.client;

import io.qameta.allure.Step;
import mipkasnow.dev.rococo.api.ArtistApi;
import mipkasnow.dev.rococo.api.RestClient;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.model.artist.ArtistResponse;
import mipkasnow.dev.rococo.model.artist.ArtistUpdateRequest;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public final class ArtistApiClient extends RestClient {

    private final ArtistApi artistApi;

    public ArtistApiClient() {
        super(CFG.gatewayUrl());
        this.artistApi = create(ArtistApi.class);
    }

    @Nonnull
    public PageResponse<ArtistResponse> findAll() {
        return findAll(ApiLoginExtension.optionalToken(), null, null, null);
    }

    @Nonnull
    public PageResponse<ArtistResponse> findAll(@Nullable Integer page,
                                                @Nullable Integer size,
                                                @Nullable String name) {
        return findAll(ApiLoginExtension.optionalToken(), page, size, name);
    }

    @Nonnull
    public ArtistResponse findById(UUID id) {
        return findById(ApiLoginExtension.optionalToken(), id);
    }

    @Nonnull
    public ArtistResponse create(ArtistCreateRequest artist) {
        return create(ApiLoginExtension.token(), artist);
    }

    @Nonnull
    public Response<ArtistResponse> createResponse(ArtistCreateRequest artist) {
        return createResponse(ApiLoginExtension.token(), artist);
    }

    @Nonnull
    public ArtistResponse update(ArtistUpdateRequest artist) {
        return update(ApiLoginExtension.token(), artist);
    }

    @Nonnull
    @Step("GET /api/artist")
    public PageResponse<ArtistResponse> findAll(@Nullable String token,
                                                @Nullable Integer page,
                                                @Nullable Integer size,
                                                @Nullable String name) {
        return ApiCalls.body(artistApi.findAll(ApiCalls.optionalBearer(token), page, size, name));
    }

    @Nonnull
    @Step("GET /api/artist/{id}")
    public ArtistResponse findById(@Nullable String token, UUID id) {
        return ApiCalls.body(artistApi.findById(ApiCalls.optionalBearer(token), id));
    }

    @Nonnull
    @Step("POST /api/artist")
    public ArtistResponse create(String token, ArtistCreateRequest artist) {
        return ApiCalls.body(artistApi.create(ApiCalls.bearer(token), artist));
    }

    @Nonnull
    @Step("POST /api/artist")
    public Response<ArtistResponse> createResponse(@Nullable String token, ArtistCreateRequest artist) {
        return ApiCalls.response(artistApi.create(ApiCalls.optionalBearer(token), artist));
    }

    @Nonnull
    @Step("PATCH /api/artist")
    public ArtistResponse update(String token, ArtistUpdateRequest artist) {
        return ApiCalls.body(artistApi.update(ApiCalls.bearer(token), artist));
    }

    @Nonnull
    @Step("PATCH /api/artist")
    public Response<ArtistResponse> updateResponse(@Nullable String token, ArtistUpdateRequest artist) {
        return ApiCalls.response(artistApi.update(ApiCalls.optionalBearer(token), artist));
    }

    @Nonnull
    @Step("GET /api/artist/{id}")
    public Response<ArtistResponse> findByIdResponse(@Nullable String token, UUID id) {
        return ApiCalls.response(artistApi.findById(ApiCalls.optionalBearer(token), id));
    }

    @Nonnull
    @Step("POST /api/artist")
    public Response<ArtistResponse> createRaw(@Nullable String token, String json) {
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        return ApiCalls.response(artistApi.createRaw(ApiCalls.optionalBearer(token), body));
    }
}
