package mipkasnow.dev.rococo.test.rest;

import mipkasnow.dev.rococo.api.client.UserApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.meta.RestTest;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.user.UserResponse;
import mipkasnow.dev.rococo.model.user.UserUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import java.util.UUID;
import java.util.stream.Stream;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;
import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomUsername;

@RestTest
public class UserRestTest {

    private UserApiClient userApiClient;

    @Test
    @DisplayName("Профиль без токена не отдаётся")
    void currentUserWithoutTokenShouldBeUnauthorized() {
        Response<UserResponse> response = userApiClient.currentResponse(null);
        RestAssert.error("без токена", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Обновление профиля без токена отклоняется")
    void updateWithoutTokenShouldBeUnauthorized() {
        Response<UserResponse> response = userApiClient.updateResponse(
                null,
                new UserUpdateRequest(UUID.randomUUID(), randomString(8), null, null, null)
        );
        RestAssert.error("без токена", response, 401, ApiMsg.CODE_UNAUTHORIZED, ApiMsg.UNAUTHORIZED);
    }

    @Test
    @ApiLogin
    @DisplayName("Чужой идентификатор в профиле отклоняется")
    void updateWithAnotherIdShouldBeForbidden() {
        UserResponse me = userApiClient.current();
        Response<UserResponse> response = userApiClient.updateResponse(
                ApiLoginExtension.token(),
                new UserUpdateRequest(UUID.randomUUID(), me.username(), null, null, null)
        );
        RestAssert.error("чужой id", response, 403, ApiMsg.CODE_USER_FORBIDDEN, ApiMsg.USER_FORBIDDEN);
    }

    @Test
    @ApiLogin
    @DisplayName("Чужой логин в профиле отклоняется")
    void updateWithAnotherUsernameShouldBeForbidden() {
        UserResponse me = userApiClient.current();
        Response<UserResponse> response = userApiClient.updateResponse(
                ApiLoginExtension.token(),
                new UserUpdateRequest(me.id(), randomUsername(), null, null, null)
        );
        RestAssert.error("чужой логин", response, 403, ApiMsg.CODE_USER_FORBIDDEN, ApiMsg.USER_FORBIDDEN);
    }

    @ParameterizedTest(name = "{displayName}: {0}")
    @ApiLogin
    @MethodSource("invalidUsers")
    @DisplayName("Обновление профиля отклоняет некорректные поля")
    void updateShouldRejectInvalidFields(UserField field) {
        UserResponse me = userApiClient.current();
        Response<UserResponse> response = userApiClient.updateResponse(ApiLoginExtension.token(), field.request(me));
        RestAssert.error(field.toString(), response, 400, ApiMsg.CODE_BAD_REQUEST, field.message());
    }

    private static Stream<UserField> invalidUsers() {
        return Stream.of(UserField.values());
    }

    private enum UserField {
        MISSING_ID("без идентификатора", ApiMsg.USER_ID_REQUIRED),
        BLANK_USERNAME("пустой логин", ApiMsg.USERNAME_REQUIRED),
        LONG_USERNAME("логин длиннее 50 символов", ApiMsg.USERNAME_LONG),
        LONG_FIRSTNAME("имя длиннее 255 символов", ApiMsg.FIRSTNAME_LONG),
        LONG_LASTNAME("фамилия длиннее 255 символов", ApiMsg.LASTNAME_LONG),
        BROKEN_AVATAR("битый аватар", ApiMsg.IMAGE_INVALID),
        UNSUPPORTED_AVATAR("недопустимый формат аватара", ApiMsg.IMAGE_FORMAT);

        private final String title;
        private final String message;

        UserField(String title, String message) {
            this.title = title;
            this.message = message;
        }

        @Override
        public String toString() {
            return title;
        }

        private String message() {
            return message;
        }

        private UserUpdateRequest request(UserResponse user) {
            return switch (this) {
                case MISSING_ID -> new UserUpdateRequest(null, user.username(), null, null, null);
                case BLANK_USERNAME -> new UserUpdateRequest(user.id(), " ", null, null, null);
                case LONG_USERNAME -> new UserUpdateRequest(user.id(), randomString(51), null, null, null);
                case LONG_FIRSTNAME -> new UserUpdateRequest(user.id(), user.username(), randomString(256), null, null);
                case LONG_LASTNAME -> new UserUpdateRequest(user.id(), user.username(), null, randomString(256), null);
                case BROKEN_AVATAR -> new UserUpdateRequest(user.id(), user.username(), null, null, Images.BROKEN);
                case UNSUPPORTED_AVATAR ->
                        new UserUpdateRequest(user.id(), user.username(), null, null, Images.UNSUPPORTED);
            };
        }
    }
}
