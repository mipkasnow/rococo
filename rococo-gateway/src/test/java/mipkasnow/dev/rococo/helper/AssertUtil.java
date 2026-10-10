package mipkasnow.dev.rococo.helper;

import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class AssertUtil {

    public static <T extends RuntimeException> void assertThrowsWithMessage(
            Class<T> expectedType,
            String expectedMessage,
            Executable executable) {

        T exception = assertThrows(expectedType, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }

    public static void assertApiException(ErrorCode expected, Executable executable) {
        ApiException exception = assertThrowsExactly(ApiException.class, executable);
        assertEquals(expected, exception.getErrorCode());
        assertEquals(expected.getMessage(), exception.getMessage());
    }
}
