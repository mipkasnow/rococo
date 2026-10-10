package mipkasnow.dev.rococo.integration.controller;

import mipkasnow.dev.rococo.dto.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.dto.artist.ArtistResponse;
import mipkasnow.dev.rococo.dto.artist.ArtistUpdateRequest;
import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.entity.LogEntity;
import mipkasnow.dev.rococo.entity.MinioBucket;
import mipkasnow.dev.rococo.exception.ApiErrorResponse;
import mipkasnow.dev.rococo.exception.ErrorCode;
import mipkasnow.dev.rococo.integration.IntegrationTest;
import mipkasnow.dev.rococo.integration.support.ApiClient;
import mipkasnow.dev.rococo.integration.support.ApiExchange;
import mipkasnow.dev.rococo.integration.support.KafkaLogReader;
import mipkasnow.dev.rococo.integration.support.PageResponse;
import mipkasnow.dev.rococo.integration.support.TestPhotos;
import mipkasnow.dev.rococo.log.ApiLogEvent;
import mipkasnow.dev.rococo.repository.ArtistRepository;
import mipkasnow.dev.rococo.repository.LogRepository;
import mipkasnow.dev.rococo.service.S3FileService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

@IntegrationTest
class ArtistControllerTest {

    private static final String ARTISTS = "/api/artist";
    private static final String USER = "test-user";
    private static final String SHISHKIN = TestPhotos.dataUrl("files/artist/Шишкин.jpg");
    private static final String VASNETSOV = TestPhotos.dataUrl("files/artist/Васнецов.jpg");
    private static final String BIOGRAPHY = "Биография художника для интеграционного теста.";

    @Autowired
    private ApiClient api;

    @Autowired
    private KafkaLogReader logs;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private S3FileService s3FileService;

    @Autowired
    private LogRepository logRepository;

    @Test
    void createAndReadArtistWithPhoto() {
        String name = unique("Шишкин");
        Instant sentAt = Instant.now();
        ArtistResponse created = create(name, SHISHKIN);
        ApiLogEvent createdLog = logs.await(POST.name(), ARTISTS, 200, USER, sentAt);

        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo(name);
        assertThat(created.biography()).isEqualTo(BIOGRAPHY);
        assertThat(created.photo()).isEqualTo(SHISHKIN);
        assertThat(createdLog.success()).isTrue();
        assertThat(createdLog.error()).isNull();
        assertPersisted(createdLog);

        Instant readAt = Instant.now();
        ArtistResponse found = api.anonymous().get(ARTISTS + "/" + created.id(), ArtistResponse.class);
        ApiLogEvent readLog = logs.await(GET.name(), ARTISTS + "/" + created.id(), 200, "anonymous", readAt);

        assertThat(found).isEqualTo(created);
        assertThat(readLog.success()).isTrue();
        assertPersisted(readLog);
        assertThat(search(name).content()).containsExactly(created);
    }

    @Test
    void minioReturnsStoredPhoto() {
        ArtistResponse created = create(unique("Шишкин"), SHISHKIN);
        ArtistEntity artist = artistRepository.findByIdOrThrow(created.id());

        assertThat(artist.getPhotoFile().getBucket()).isEqualTo(MinioBucket.artist);
        assertThat(s3FileService.toDataUrl(artist.getPhotoFile())).isEqualTo(SHISHKIN);
    }

    @Test
    void updateReplacesTextAndPhoto() {
        ArtistResponse created = create(unique("Васнецов"), VASNETSOV);
        String newName = unique("Шишкин");
        String newBiography = "Обновлённая биография художника для теста.";

        ArtistResponse updated = api.as(USER).send(
                PATCH,
                ARTISTS,
                new ArtistUpdateRequest(created.id(), newName, newBiography, SHISHKIN),
                ArtistResponse.class
        );

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.name()).isEqualTo(newName);
        assertThat(updated.biography()).isEqualTo(newBiography);
        assertThat(updated.photo()).isEqualTo(SHISHKIN);
        assertThat(api.anonymous().get(ARTISTS + "/" + created.id(), ArtistResponse.class)).isEqualTo(updated);
        assertThat(search(created.name()).content()).isEmpty();
        assertThat(search(newName).content()).containsExactly(updated);
    }

    @Test
    void duplicateNameIsRejected() {
        String name = unique("Шишкин");
        create(name, SHISHKIN);

        Instant sentAt = Instant.now();
        ApiExchange response = api.as(USER).exchange(POST, ARTISTS, new ArtistCreateRequest(name, BIOGRAPHY, VASNETSOV));
        ApiErrorResponse error = response.as(ApiErrorResponse.class);
        ApiLogEvent event = logs.await(POST.name(), ARTISTS, 409, USER, sentAt);

        assertThat(response.status()).isEqualTo(409);
        assertThat(error.errorCode()).isEqualTo(ErrorCode.ArtistNameTaken);
        assertThat(event.success()).isFalse();
        assertThat(event.error()).contains(ErrorCode.ArtistNameTaken.name());
        assertPersisted(event);
    }

    @Test
    void renameToExistingNameIsRejected() {
        ArtistResponse first = create(unique("Шишкин"), SHISHKIN);
        ArtistResponse second = create(unique("Васнецов"), VASNETSOV);

        ApiExchange response = api.as(USER).exchange(
                PATCH,
                ARTISTS,
                new ArtistUpdateRequest(first.id(), second.name(), BIOGRAPHY, SHISHKIN)
        );

        assertThat(response.status()).isEqualTo(409);
        assertThat(response.as(ApiErrorResponse.class).errorCode()).isEqualTo(ErrorCode.ArtistNameTaken);
        assertThat(api.anonymous().get(ARTISTS + "/" + first.id(), ArtistResponse.class).name()).isEqualTo(first.name());
    }

    @Test
    void unknownArtistIsNotFound() {
        ApiExchange response = api.anonymous().exchange(GET, ARTISTS + "/" + UUID.randomUUID(), null);

        assertThat(response.status()).isEqualTo(404);
        assertThat(response.as(ApiErrorResponse.class).errorCode()).isEqualTo(ErrorCode.ArtistNotFound);
    }

    @Test
    void anonymousCreateIsRejected() {
        ApiExchange response = api.anonymous().exchange(
                POST,
                ARTISTS,
                new ArtistCreateRequest(unique("Шишкин"), BIOGRAPHY, SHISHKIN)
        );

        assertThat(response.status()).isEqualTo(401);
        assertThat(response.as(ApiErrorResponse.class).errorCode()).isEqualTo(ErrorCode.Unauthorized);
    }

    @Test
    void invalidArtistIsRejected() {
        ApiExchange response = api.as(USER).exchange(
                POST,
                ARTISTS,
                new ArtistCreateRequest("аб", "коротко", "data:image/jpeg;base64,bm90LWFuLWltYWdl")
        );
        ApiErrorResponse error = response.as(ApiErrorResponse.class);

        assertThat(response.status()).isEqualTo(400);
        assertThat(error.errorCode()).isEqualTo(ErrorCode.BadRequest);
        assertThat(error.errors()).contains(
                "Имя художника не может быть короче 3 символов",
                "Биография художника не может быть короче 10 символов",
                "Допустимые форматы изображения: PNG, JPEG, GIF, WEBP"
        );
    }

    @Test
    void searchIsCaseInsensitiveAndSorted() {
        String token = UUID.randomUUID().toString();
        String first = "A " + token;
        String second = "B " + token;
        create(first, SHISHKIN);
        create(second, VASNETSOV);

        PageResponse<ArtistResponse> page = search(token.toUpperCase());

        assertThat(page.content()).extracting(ArtistResponse::name).containsExactly(first, second);
    }

    private ArtistResponse create(String name, String photo) {
        return api.as(USER).send(POST, ARTISTS, new ArtistCreateRequest(name, BIOGRAPHY, photo), ArtistResponse.class);
    }

    private PageResponse<ArtistResponse> search(String name) {
        return api.anonymous().get(ARTISTS, api.pageOf(ArtistResponse.class), builder -> builder
                .param("name", name)
                .param("page", "0")
                .param("size", "50"));
    }

    private String unique(String label) {
        return label + " " + UUID.randomUUID();
    }

    private void assertPersisted(ApiLogEvent event) {
        LogEntity stored = awaitLog(event.id());
        assertThat(stored.getOccurredAt()).isEqualTo(event.occurredAt());
        assertThat(stored.getIp()).isEqualTo(event.ip());
        assertThat(stored.getUsername()).isEqualTo(event.username());
        assertThat(stored.getHttpMethod()).isEqualTo(event.httpMethod());
        assertThat(stored.getPath()).isEqualTo(event.path());
        assertThat(stored.getHttpStatus()).isEqualTo(event.httpStatus());
        assertThat(stored.isSuccess()).isEqualTo(event.success());
        assertThat(stored.getError()).isEqualTo(event.error());
    }

    private LogEntity awaitLog(UUID id) {
        AtomicReference<LogEntity> stored = new AtomicReference<>();
        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> stored.set(logRepository.findById(id).orElseThrow()));
        return stored.get();
    }
}
