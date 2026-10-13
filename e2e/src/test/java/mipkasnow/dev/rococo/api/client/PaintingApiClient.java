package mipkasnow.dev.rococo.api.client;

import io.qameta.allure.Step;
import mipkasnow.dev.rococo.api.PaintingApi;
import mipkasnow.dev.rococo.api.RestClient;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.painting.PaintingCreateRequest;
import mipkasnow.dev.rococo.model.painting.PaintingResponse;
import mipkasnow.dev.rococo.model.painting.PaintingUpdateRequest;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public final class PaintingApiClient extends RestClient {

    private final PaintingApi paintingApi;

    public PaintingApiClient() {
        super(CFG.gatewayUrl());
        this.paintingApi = create(PaintingApi.class);
    }

    @Nonnull
    public PageResponse<PaintingResponse> findAll() {
        return findAll(ApiLoginExtension.optionalToken(), null, null, null);
    }

    @Nonnull
    public PageResponse<PaintingResponse> findAll(@Nullable Integer page,
                                                  @Nullable Integer size,
                                                  @Nullable String title) {
        return findAll(ApiLoginExtension.optionalToken(), page, size, title);
    }

    @Nonnull
    public PageResponse<PaintingResponse> findByArtist(UUID artistId,
                                                       @Nullable Integer page,
                                                       @Nullable Integer size) {
        return findByArtist(ApiLoginExtension.optionalToken(), artistId, page, size);
    }

    @Nonnull
    public PaintingResponse findById(UUID id) {
        return findById(ApiLoginExtension.optionalToken(), id);
    }

    @Nonnull
    public PaintingResponse create(PaintingCreateRequest painting) {
        return create(ApiLoginExtension.token(), painting);
    }

    @Nonnull
    public PaintingResponse update(PaintingUpdateRequest painting) {
        return update(ApiLoginExtension.token(), painting);
    }

    @Nonnull
    @Step("GET /api/painting")
    public PageResponse<PaintingResponse> findAll(@Nullable String token,
                                                  @Nullable Integer page,
                                                  @Nullable Integer size,
                                                  @Nullable String title) {
        return ApiCalls.body(paintingApi.findAll(ApiCalls.optionalBearer(token), page, size, title));
    }

    @Nonnull
    @Step("GET /api/painting/author/{artistId}")
    public PageResponse<PaintingResponse> findByArtist(@Nullable String token,
                                                       UUID artistId,
                                                       @Nullable Integer page,
                                                       @Nullable Integer size) {
        return ApiCalls.body(paintingApi.findByArtist(ApiCalls.optionalBearer(token), artistId, page, size));
    }

    @Nonnull
    @Step("GET /api/painting/{id}")
    public PaintingResponse findById(@Nullable String token, UUID id) {
        return ApiCalls.body(paintingApi.findById(ApiCalls.optionalBearer(token), id));
    }

    @Nonnull
    @Step("POST /api/painting")
    public PaintingResponse create(String token, PaintingCreateRequest painting) {
        return ApiCalls.body(paintingApi.create(ApiCalls.bearer(token), painting));
    }

    @Nonnull
    @Step("PATCH /api/painting")
    public PaintingResponse update(String token, PaintingUpdateRequest painting) {
        return ApiCalls.body(paintingApi.update(ApiCalls.bearer(token), painting));
    }

    @Nonnull
    @Step("POST /api/painting")
    public Response<PaintingResponse> createResponse(@Nullable String token, PaintingCreateRequest painting) {
        return ApiCalls.response(paintingApi.create(ApiCalls.optionalBearer(token), painting));
    }

    @Nonnull
    @Step("PATCH /api/painting")
    public Response<PaintingResponse> updateResponse(@Nullable String token, PaintingUpdateRequest painting) {
        return ApiCalls.response(paintingApi.update(ApiCalls.optionalBearer(token), painting));
    }

    @Nonnull
    @Step("GET /api/painting/{id}")
    public Response<PaintingResponse> findByIdResponse(@Nullable String token, UUID id) {
        return ApiCalls.response(paintingApi.findById(ApiCalls.optionalBearer(token), id));
    }
}
