package mipkasnow.dev.rococo.test.rest;

import mipkasnow.dev.rococo.api.client.MuseumApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.Museum;
import mipkasnow.dev.rococo.jupiter.annotation.meta.RestTest;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.country.CountryRequest;
import mipkasnow.dev.rococo.model.geo.GeoRequest;
import mipkasnow.dev.rococo.model.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.model.museum.MuseumUpdateRequest;
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
public class MuseumRestTest {

    private MuseumApiClient museumApiClient;

    @Test
    @DisplayName("Создание музея без токена отклоняется")
    void createWithoutTokenShouldBeUnauthorized() {
        Response<MuseumResponse> response = museumApiClient.createResponse(null, validMuseum());
        RestAssert.error("без токена", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Обновление музея без токена отклоняется")
    void updateWithoutTokenShouldBeUnauthorized() {
        Response<MuseumResponse> response = museumApiClient.updateResponse(null, validUpdate(UUID.randomUUID()));
        RestAssert.error("без токена", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Несуществующий музей не находится")
    void unknownMuseumShouldNotBeFound() {
        Response<MuseumResponse> response = museumApiClient.findByIdResponse(null, UUID.randomUUID());
        RestAssert.error("не найден", response, 404, ApiMsg.CODE_MUSEUM_NOT_FOUND, ApiMsg.MUSEUM_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление несуществующего музея отклоняется")
    void updateUnknownMuseumShouldNotBeFound() {
        Response<MuseumResponse> response = museumApiClient.updateResponse(token(), validUpdate(UUID.randomUUID()));
        RestAssert.error("не найден", response, 404, ApiMsg.CODE_MUSEUM_NOT_FOUND, ApiMsg.MUSEUM_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @DisplayName("Музей с неизвестной страной не создаётся")
    void createWithUnknownCountryShouldNotBeFound() {
        MuseumCreateRequest request = new MuseumCreateRequest(
                randomString(12),
                randomString(20),
                Images.museum(),
                geo(randomString(8), UUID.randomUUID())
        );
        Response<MuseumResponse> response = museumApiClient.createResponse(token(), request);
        RestAssert.error("неизвестная страна", response, 404, ApiMsg.CODE_COUNTRY_NOT_FOUND, ApiMsg.COUNTRY_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @Museum
    @DisplayName("Смена страны музея на неизвестную отклоняется")
    void updateWithUnknownCountryShouldNotBeFound(MuseumResponse museum) {
        MuseumUpdateRequest request = new MuseumUpdateRequest(
                museum.id(),
                museum.title(),
                museum.description(),
                museum.photo(),
                geo(museum.geo().city(), UUID.randomUUID())
        );
        Response<MuseumResponse> response = museumApiClient.updateResponse(token(), request);
        RestAssert.error("неизвестная страна", response, 404, ApiMsg.CODE_COUNTRY_NOT_FOUND, ApiMsg.COUNTRY_NOT_FOUND);
    }

    @Test
    @ApiLogin
    @Museum
    @DisplayName("Музей с занятым названием не создаётся")
    void duplicateTitleShouldConflict(MuseumResponse museum) {
        MuseumCreateRequest request = new MuseumCreateRequest(
                museum.title(),
                randomString(20),
                Images.museum(),
                geo(randomString(8), museum.geo().country().id())
        );
        Response<MuseumResponse> response = museumApiClient.createResponse(token(), request);
        RestAssert.error("занятое название", response, 409, ApiMsg.CODE_MUSEUM_TITLE_TAKEN, ApiMsg.MUSEUM_TITLE_TAKEN);
    }

    @Test
    @ApiLogin
    @Museum
    @DisplayName("Переименование музея в занятое название отклоняется")
    void updateToTakenTitleShouldConflict(MuseumResponse museum) {
        MuseumResponse another = museumApiClient.create(new MuseumCreateRequest(
                randomString(12),
                randomString(20),
                Images.museum(),
                geo(randomString(8), museum.geo().country().id())
        ));
        MuseumUpdateRequest request = new MuseumUpdateRequest(
                another.id(),
                museum.title(),
                another.description(),
                another.photo(),
                geo(another.geo().city(), another.geo().country().id())
        );
        Response<MuseumResponse> response = museumApiClient.updateResponse(token(), request);
        RestAssert.error("занятое название", response, 409, ApiMsg.CODE_MUSEUM_TITLE_TAKEN, ApiMsg.MUSEUM_TITLE_TAKEN);
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ApiLogin
    @MethodSource("invalidCreates")
    @DisplayName("Создание музея отклоняет некорректные поля")
    void createShouldRejectInvalidFields(String caseName, MuseumCreateRequest request, String message) {
        Response<MuseumResponse> response = museumApiClient.createResponse(token(), request);
        RestAssert.error(caseName, response, 400, ApiMsg.CODE_BAD_REQUEST, message);
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ApiLogin
    @MethodSource("invalidUpdates")
    @DisplayName("Обновление музея отклоняет некорректные поля")
    void updateShouldRejectInvalidFields(String caseName, MuseumUpdateRequest request, String message) {
        Response<MuseumResponse> response = museumApiClient.updateResponse(token(), request);
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
                new MuseumUpdateRequest(null, randomString(8), randomString(20), Images.museum(), validGeo()),
                ApiMsg.MUSEUM_ID_REQUIRED
        );
        return Stream.concat(fields, Stream.of(missingId));
    }

    private static Stream<MuseumField> invalidFields() {
        String title = randomString(8);
        String description = randomString(20);
        String photo = Images.museum();
        GeoRequest geo = validGeo();
        return Stream.of(
                new MuseumField("название короче 3 символов", randomString(2), description, photo, geo, ApiMsg.MUSEUM_TITLE_SHORT),
                new MuseumField("название длиннее 255 символов", randomString(256), description, photo, geo, ApiMsg.MUSEUM_TITLE_LONG),
                new MuseumField("пустое название", " ", description, photo, geo, ApiMsg.MUSEUM_TITLE_REQUIRED),
                new MuseumField("описание короче 10 символов", title, randomString(9), photo, geo, ApiMsg.MUSEUM_DESCRIPTION_SHORT),
                new MuseumField("описание длиннее 1000 символов", title, randomString(1001), photo, geo, ApiMsg.MUSEUM_DESCRIPTION_LONG),
                new MuseumField("пустое описание", title, " ", photo, geo, ApiMsg.MUSEUM_DESCRIPTION_REQUIRED),
                new MuseumField("без фото", title, description, null, geo, ApiMsg.MUSEUM_PHOTO_REQUIRED),
                new MuseumField("битое фото", title, description, Images.BROKEN, geo, ApiMsg.IMAGE_INVALID),
                new MuseumField("недопустимый формат фото", title, description, Images.UNSUPPORTED, geo, ApiMsg.IMAGE_FORMAT),
                new MuseumField("город короче 3 символов", title, description, photo, geo(randomString(2), UUID.randomUUID()), ApiMsg.CITY_SHORT),
                new MuseumField("город длиннее 255 символов", title, description, photo, geo(randomString(256), UUID.randomUUID()), ApiMsg.CITY_LONG),
                new MuseumField("пустой город", title, description, photo, geo(" ", UUID.randomUUID()), ApiMsg.CITY_REQUIRED),
                new MuseumField("без географии", title, description, photo, null, ApiMsg.COUNTRY_REQUIRED),
                new MuseumField("без страны", title, description, photo, new GeoRequest(randomString(8), null), ApiMsg.COUNTRY_REQUIRED),
                new MuseumField("без идентификатора страны", title, description, photo, new GeoRequest(randomString(8), new CountryRequest(null)), ApiMsg.COUNTRY_REQUIRED)
        );
    }

    private static MuseumCreateRequest validMuseum() {
        return new MuseumCreateRequest(randomString(8), randomString(20), Images.museum(), validGeo());
    }

    private static MuseumUpdateRequest validUpdate(UUID id) {
        return new MuseumUpdateRequest(id, randomString(8), randomString(20), Images.museum(), validGeo());
    }

    private static GeoRequest validGeo() {
        return geo(randomString(8), UUID.randomUUID());
    }

    private static GeoRequest geo(String city, UUID countryId) {
        return new GeoRequest(city, new CountryRequest(countryId));
    }

    private static String token() {
        return ApiLoginExtension.token();
    }

    private record MuseumField(
            String caseName,
            String title,
            String description,
            String photo,
            GeoRequest geo,
            String message
    ) {

        private MuseumCreateRequest create() {
            return new MuseumCreateRequest(title, description, photo, geo);
        }

        private MuseumUpdateRequest update() {
            return new MuseumUpdateRequest(UUID.randomUUID(), title, description, photo, geo);
        }
    }
}
