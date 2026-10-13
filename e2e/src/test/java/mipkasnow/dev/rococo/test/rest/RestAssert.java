package mipkasnow.dev.rococo.test.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import mipkasnow.dev.rococo.model.ApiErrorResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UtilityClass
class RestAssert {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static void error(String title, Response<?> response, int status, String errorCode, String message) {
        assertEquals(status, response.code(), title);
        ApiErrorResponse error = read(title, response);
        assertEquals(errorCode, error.errorCode(), title);
        assertTrue(error.errors() != null && error.errors().contains(message), () -> title + ": " + error.errors());
    }

    private static ApiErrorResponse read(String title, Response<?> response) {
        try (ResponseBody body = response.errorBody()) {
            if (body == null) {
                throw new AssertionError(title + ": пустое тело ошибки, статус " + response.code());
            }
            return requireNonNull(MAPPER.readValue(body.string(), ApiErrorResponse.class));
        } catch (IOException exception) {
            throw new AssertionError(title, exception);
        }
    }
}
