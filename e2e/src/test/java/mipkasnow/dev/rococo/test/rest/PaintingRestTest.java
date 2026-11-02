package mipkasnow.dev.rococo.test.rest;

import io.qameta.allure.Feature;
import mipkasnow.dev.rococo.api.client.PaintingApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.Artist;
import mipkasnow.dev.rococo.jupiter.annotation.Museum;
import mipkasnow.dev.rococo.jupiter.annotation.Painting;
import mipkasnow.dev.rococo.jupiter.annotation.meta.RestTest;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.artist.ArtistIdRequest;
import mipkasnow.dev.rococo.model.artist.ArtistResponse;
import mipkasnow.dev.rococo.model.museum.MuseumIdRequest;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.model.painting.PaintingCreateRequest;
import mipkasnow.dev.rococo.model.painting.PaintingResponse;
import mipkasnow.dev.rococo.model.painting.PaintingUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import java.util.UUID;
import java.util.stream.Stream;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@RestTest
@Feature("Картины")
public class PaintingRestTest {

    private PaintingApiClient paintingApiClient;

    @Test
    @DisplayName("Создание картины без токена отклоняется")
    void createWithoutTokenShouldBeUnauthorized() {
        Response<PaintingResponse> response = paintingApiClient.createResponse(null, validPainting());
        RestAssert.error("без токена", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Обновление картины без токена отклоняется")
    void updateWithoutTokenShouldBeUnauthorized() {
        Response<PaintingResponse> response = paintingApiClient.updateResponse(null, validUpdate(UUID.randomUUID()));
        RestAssert.error("без токена", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Несуществующая картина не находится")
    void unknownPaintingShouldNotBeFound() {
        Response<PaintingResponse> response = paintingApiClient.findByIdResponse(null, UUID.randomUUID());
        RestAssert.error("не найдена", response, 404, ApiMsg.CODE_PAINTING_NOT_FOUND, ApiMsg.PAINTING_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление несуществующей картины отклоняется")
    void updateUnknownPaintingShouldNotBeFound() {
        Response<PaintingResponse> response = paintingApiClient.updateResponse(token(), validUpdate(UUID.randomUUID()));
        RestAssert.error("не найдена", response, 404, ApiMsg.CODE_PAINTING_NOT_FOUND, ApiMsg.PAINTING_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @DisplayName("Картина с неизвестным автором не создаётся")
    void createWithUnknownArtistShouldNotBeFound() {
        PaintingCreateRequest request = new PaintingCreateRequest(
                randomString(12),
                randomString(20),
                Images.painting(),
                new ArtistIdRequest(UUID.randomUUID()),
                null
        );
        Response<PaintingResponse> response = paintingApiClient.createResponse(token(), request);
        RestAssert.error("неизвестный автор", response, 404, ApiMsg.CODE_ARTIST_NOT_FOUND, ApiMsg.ARTIST_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @Artist
    @DisplayName("Картина с неизвестным музеем не создаётся")
    void createWithUnknownMuseumShouldNotBeFound(ArtistResponse artist) {
        PaintingCreateRequest request = new PaintingCreateRequest(
                randomString(12),
                randomString(20),
                Images.painting(),
                new ArtistIdRequest(artist.id()),
                new MuseumIdRequest(UUID.randomUUID())
        );
        Response<PaintingResponse> response = paintingApiClient.createResponse(token(), request);
        RestAssert.error("неизвестный музей", response, 404, ApiMsg.CODE_MUSEUM_NOT_FOUND, ApiMsg.MUSEUM_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @Artist
    @Museum
    @Painting
    @DisplayName("Смена автора картины на неизвестного отклоняется")
    void updateWithUnknownArtistShouldNotBeFound(PaintingResponse painting, MuseumResponse museum) {
        PaintingUpdateRequest request = new PaintingUpdateRequest(
                painting.id(),
                painting.title(),
                painting.description(),
                painting.content(),
                new ArtistIdRequest(UUID.randomUUID()),
                new MuseumIdRequest(museum.id())
        );
        Response<PaintingResponse> response = paintingApiClient.updateResponse(token(), request);
        RestAssert.error("неизвестный автор", response, 404, ApiMsg.CODE_ARTIST_NOT_FOUND, ApiMsg.ARTIST_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @Artist
    @Museum
    @Painting
    @DisplayName("Смена музея картины на неизвестный отклоняется")
    void updateWithUnknownMuseumShouldNotBeFound(ArtistResponse artist, PaintingResponse painting) {
        PaintingUpdateRequest request = new PaintingUpdateRequest(
                painting.id(),
                painting.title(),
                painting.description(),
                painting.content(),
                new ArtistIdRequest(artist.id()),
                new MuseumIdRequest(UUID.randomUUID())
        );
        Response<PaintingResponse> response = paintingApiClient.updateResponse(token(), request);
        RestAssert.error("неизвестный музей", response, 404, ApiMsg.CODE_MUSEUM_NOT_FOUND, ApiMsg.MUSEUM_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @Artist
    @Museum
    @Painting
    @DisplayName("Картина с занятым названием не создаётся")
    void duplicateTitleShouldConflict(ArtistResponse artist, MuseumResponse museum, PaintingResponse painting) {
        PaintingCreateRequest request = new PaintingCreateRequest(
                painting.title(),
                randomString(20),
                Images.painting(),
                new ArtistIdRequest(artist.id()),
                new MuseumIdRequest(museum.id())
        );
        Response<PaintingResponse> response = paintingApiClient.createResponse(token(), request);
        RestAssert.error("занятое название", response, 409, ApiMsg.CODE_PAINTING_TITLE_TAKEN, ApiMsg.PAINTING_TITLE_TAKEN);
    }

    @Test
    @ApiLogin
    @Artist
    @Museum
    @Painting
    @DisplayName("Переименование картины в занятое название отклоняется")
    void updateToTakenTitleShouldConflict(ArtistResponse artist, MuseumResponse museum, PaintingResponse painting) {
        PaintingResponse another = paintingApiClient.create(new PaintingCreateRequest(
                randomString(12),
                randomString(20),
                Images.painting(),
                new ArtistIdRequest(artist.id()),
                new MuseumIdRequest(museum.id())
        ));
        PaintingUpdateRequest request = new PaintingUpdateRequest(
                another.id(),
                painting.title(),
                randomString(20),
                Images.painting(),
                new ArtistIdRequest(artist.id()),
                new MuseumIdRequest(museum.id())
        );
        Response<PaintingResponse> response = paintingApiClient.updateResponse(token(), request);
        RestAssert.error("занятое название", response, 409, ApiMsg.CODE_PAINTING_TITLE_TAKEN, ApiMsg.PAINTING_TITLE_TAKEN);
    }

    @ParameterizedTest(name = "{0}")
    @ApiLogin
    @MethodSource("invalidCreates")
    @DisplayName("Создание картины отклоняет некорректные поля")
    void createShouldRejectInvalidFields(String caseName, PaintingCreateRequest request, String message) {
        Response<PaintingResponse> response = paintingApiClient.createResponse(token(), request);
        RestAssert.error(caseName, response, 400, ApiMsg.CODE_BAD_REQUEST, message);
    }

    @ParameterizedTest(name = "{0}")
    @ApiLogin
    @MethodSource("invalidUpdates")
    @DisplayName("Обновление картины отклоняет некорректные поля")
    void updateShouldRejectInvalidFields(String caseName, PaintingUpdateRequest request, String message) {
        Response<PaintingResponse> response = paintingApiClient.updateResponse(token(), request);
        RestAssert.error(caseName, response, 400, ApiMsg.CODE_BAD_REQUEST, message);
    }

    private static Stream<Arguments> invalidCreates() {
        return invalidFields().map(field -> arguments(field.caseName(), field.create(), field.message()));
    }

    private static Stream<Arguments> invalidUpdates() {
        Stream<Arguments> fields = invalidFields()
                .map(field -> arguments(field.caseName(), field.update(), field.message()));
        Arguments missingId = arguments(
                "без идентификатора",
                new PaintingUpdateRequest(null, randomString(8), randomString(20), Images.painting(), artist(), null),
                ApiMsg.PAINTING_ID_REQUIRED
        );
        return Stream.concat(fields, Stream.of(missingId));
    }

    private static Stream<PaintingField> invalidFields() {
        String title = randomString(8);
        String description = randomString(20);
        String content = Images.painting();
        ArtistIdRequest artist = artist();
        return Stream.of(
                new PaintingField("название короче 3 символов", randomString(2), description, content, artist, ApiMsg.PAINTING_TITLE_SHORT),
                new PaintingField("название длиннее 255 символов", randomString(256), description, content, artist, ApiMsg.PAINTING_TITLE_LONG),
                new PaintingField("пустое название", " ", description, content, artist, ApiMsg.PAINTING_TITLE_REQUIRED),
                new PaintingField("описание короче 10 символов", title, randomString(9), content, artist, ApiMsg.PAINTING_DESCRIPTION_SHORT),
                new PaintingField("описание длиннее 1000 символов", title, randomString(1001), content, artist, ApiMsg.PAINTING_DESCRIPTION_LONG),
                new PaintingField("пустое описание", title, " ", content, artist, ApiMsg.PAINTING_DESCRIPTION_REQUIRED),
                new PaintingField("без изображения", title, description, null, artist, ApiMsg.PAINTING_CONTENT_REQUIRED),
                new PaintingField("битое изображение", title, description, Images.BROKEN, artist, ApiMsg.IMAGE_INVALID),
                new PaintingField("недопустимый формат изображения", title, description, Images.UNSUPPORTED, artist, ApiMsg.IMAGE_FORMAT),
                new PaintingField("без автора", title, description, content, null, ApiMsg.ARTIST_REQUIRED),
                new PaintingField("без идентификатора автора", title, description, content, new ArtistIdRequest(null), ApiMsg.ARTIST_REQUIRED)
        );
    }

    private static PaintingCreateRequest validPainting() {
        return new PaintingCreateRequest(randomString(8), randomString(20), Images.painting(), artist(), null);
    }

    private static PaintingUpdateRequest validUpdate(UUID id) {
        return new PaintingUpdateRequest(id, randomString(8), randomString(20), Images.painting(), artist(), null);
    }

    private static ArtistIdRequest artist() {
        return new ArtistIdRequest(UUID.randomUUID());
    }

    private static String token() {
        return ApiLoginExtension.token();
    }

    private record PaintingField(
            String caseName,
            String title,
            String description,
            String content,
            ArtistIdRequest artist,
            String message
    ) {

        private PaintingCreateRequest create() {
            return new PaintingCreateRequest(title, description, content, artist, null);
        }

        private PaintingUpdateRequest update() {
            return new PaintingUpdateRequest(UUID.randomUUID(), title, description, content, artist, null);
        }
    }
}
