package mipkasnow.dev.rococo.test.rest;

import mipkasnow.dev.rococo.api.client.ArtistApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.Artist;
import mipkasnow.dev.rococo.jupiter.annotation.meta.RestTest;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.model.artist.ArtistResponse;
import mipkasnow.dev.rococo.model.artist.ArtistUpdateRequest;
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
public class ArtistRestTest {

    private ArtistApiClient artistApiClient;

    @Test
    @DisplayName("Создание художника без токена отклоняется")
    void createWithoutTokenShouldBeUnauthorized() {
        Response<ArtistResponse> response = artistApiClient.createResponse(null, validArtist());
        RestAssert.error("без токена", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Обновление художника без токена отклоняется")
    void updateWithoutTokenShouldBeUnauthorized() {
        Response<ArtistResponse> response = artistApiClient.updateResponse(null, validUpdate(UUID.randomUUID()));
        RestAssert.error("без токена", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Несуществующий художник не находится")
    void unknownArtistShouldNotBeFound() {
        Response<ArtistResponse> response = artistApiClient.findByIdResponse(null, UUID.randomUUID());
        RestAssert.error("не найден", response, 404, ApiMsg.CODE_ARTIST_NOT_FOUND, ApiMsg.ARTIST_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление несуществующего художника отклоняется")
    void updateUnknownArtistShouldNotBeFound() {
        Response<ArtistResponse> response = artistApiClient.updateResponse(token(), validUpdate(UUID.randomUUID()));
        RestAssert.error("не найден", response, 404, ApiMsg.CODE_ARTIST_NOT_FOUND, ApiMsg.ARTIST_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @Artist
    @DisplayName("Художник с занятым именем не создаётся")
    void duplicateNameShouldConflict(ArtistResponse artist) {
        ArtistCreateRequest request = new ArtistCreateRequest(artist.name(), randomString(20), Images.artist());
        Response<ArtistResponse> response = artistApiClient.createResponse(token(), request);
        RestAssert.error("занятое имя", response, 409, ApiMsg.CODE_ARTIST_NAME_TAKEN, ApiMsg.ARTIST_NAME_TAKEN);
    }

    @Test
    @ApiLogin
    @Artist
    @DisplayName("Переименование в занятое имя отклоняется")
    void updateToTakenNameShouldConflict(ArtistResponse artist) {
        ArtistResponse another = artistApiClient.create(validArtist());
        ArtistUpdateRequest request = new ArtistUpdateRequest(another.id(), artist.name(), randomString(20), Images.artist());
        Response<ArtistResponse> response = artistApiClient.updateResponse(token(), request);
        RestAssert.error("занятое имя", response, 409, ApiMsg.CODE_ARTIST_NAME_TAKEN, ApiMsg.ARTIST_NAME_TAKEN);
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ApiLogin
    @MethodSource("invalidCreates")
    @DisplayName("Создание художника отклоняет некорректные поля")
    void createShouldRejectInvalidFields(String caseName, ArtistCreateRequest request, String message) {
        Response<ArtistResponse> response = artistApiClient.createResponse(token(), request);
        RestAssert.error(caseName, response, 400, ApiMsg.CODE_BAD_REQUEST, message);
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ApiLogin
    @MethodSource("invalidUpdates")
    @DisplayName("Обновление художника отклоняет некорректные поля")
    void updateShouldRejectInvalidFields(String caseName, ArtistUpdateRequest request, String message) {
        Response<ArtistResponse> response = artistApiClient.updateResponse(token(), request);
        RestAssert.error(caseName, response, 400, ApiMsg.CODE_BAD_REQUEST, message);
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ApiLogin
    @MethodSource("unreadableBodies")
    @DisplayName("Нечитаемое тело создания художника отклоняется")
    void unreadableBodyShouldBeBadRequest(String caseName, String json) {
        Response<ArtistResponse> response = artistApiClient.createRaw(token(), json);
        RestAssert.error(caseName, response, 400, ApiMsg.CODE_BAD_REQUEST, ApiMsg.BAD_BODY);
    }

    private static Stream<Arguments> invalidCreates() {
        return invalidFields().map(field -> arguments(field.caseName(), field.create(), field.message()));
    }

    private static Stream<Arguments> invalidUpdates() {
        Stream<Arguments> fields = invalidFields()
                .map(field -> arguments(field.caseName(), field.update(), field.message()));
        Arguments missingId = arguments(
                "без идентификатора",
                new ArtistUpdateRequest(null, randomString(8), randomString(20), Images.artist()),
                ApiMsg.ARTIST_ID_REQUIRED
        );
        return Stream.concat(fields, Stream.of(missingId));
    }

    private static Stream<Arguments> unreadableBodies() {
        return Stream.of(
                arguments("пустое тело", ""),
                arguments("не json", "{")
        );
    }

    private static Stream<ArtistField> invalidFields() {
        String name = randomString(8);
        String biography = randomString(20);
        String photo = Images.artist();
        return Stream.of(
                new ArtistField("имя короче 3 символов", randomString(2), biography, photo, ApiMsg.ARTIST_NAME_SHORT),
                new ArtistField("имя длиннее 255 символов", randomString(256), biography, photo, ApiMsg.ARTIST_NAME_LONG),
                new ArtistField("пустое имя", " ", biography, photo, ApiMsg.ARTIST_NAME_REQUIRED),
                new ArtistField("биография короче 10 символов", name, randomString(9), photo, ApiMsg.ARTIST_BIO_SHORT),
                new ArtistField("биография длиннее 2000 символов", name, randomString(2001), photo, ApiMsg.ARTIST_BIO_LONG),
                new ArtistField("пустая биография", name, " ", photo, ApiMsg.ARTIST_BIO_REQUIRED),
                new ArtistField("без фото", name, biography, null, ApiMsg.ARTIST_PHOTO_REQUIRED),
                new ArtistField("битое фото", name, biography, Images.BROKEN, ApiMsg.IMAGE_INVALID),
                new ArtistField("недопустимый формат фото", name, biography, Images.UNSUPPORTED, ApiMsg.IMAGE_FORMAT)
        );
    }

    private static ArtistCreateRequest validArtist() {
        return new ArtistCreateRequest(randomString(8), randomString(20), Images.artist());
    }

    private static ArtistUpdateRequest validUpdate(UUID id) {
        return new ArtistUpdateRequest(id, randomString(8), randomString(20), Images.artist());
    }

    private static String token() {
        return ApiLoginExtension.token();
    }

    private record ArtistField(String caseName, String name, String biography, String photo, String message) {

        private ArtistCreateRequest create() {
            return new ArtistCreateRequest(name, biography, photo);
        }

        private ArtistUpdateRequest update() {
            return new ArtistUpdateRequest(UUID.randomUUID(), name, biography, photo);
        }
    }
}
