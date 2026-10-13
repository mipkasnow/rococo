package mipkasnow.dev.rococo.api.client;

import lombok.experimental.UtilityClass;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
class ApiCalls {

    @Nonnull
    static <T> T body(Call<T> call) {
        Response<T> response = response(call);
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Nonnull
    static <T> Response<T> response(Call<T> call) {
        try {
            return call.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Nonnull
    static String bearer(String token) {
        return requireNonNull(optionalBearer(token));
    }

    @Nullable
    static String optionalBearer(@Nullable String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }
}
