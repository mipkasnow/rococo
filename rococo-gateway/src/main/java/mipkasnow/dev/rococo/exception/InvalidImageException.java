package mipkasnow.dev.rococo.exception;

public class InvalidImageException extends ApiException {

    public InvalidImageException(String message) {
        super(ErrorCode.InvalidImage, message);
    }

    public InvalidImageException(String message, Throwable cause) {
        super(ErrorCode.InvalidImage, message, cause);
    }
}
