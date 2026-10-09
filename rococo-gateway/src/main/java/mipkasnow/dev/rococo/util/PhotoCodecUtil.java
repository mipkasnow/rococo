package mipkasnow.dev.rococo.util;

import lombok.experimental.UtilityClass;
import mipkasnow.dev.rococo.dto.file.DecodedPhoto;
import mipkasnow.dev.rococo.exception.InvalidImageException;

import java.util.Arrays;
import java.util.Base64;

@UtilityClass
public class PhotoCodecUtil {

    private static final int MAX_PHOTO_BYTES = 15 * 1024 * 1024;
    private static final int MAX_PAYLOAD_CHARS = (MAX_PHOTO_BYTES + 2) / 3 * 4;
    private static final int MAX_PAYLOAD_LENGTH = MAX_PAYLOAD_CHARS * 2;
    private static final String DATA_IMAGE_PREFIX = "data:image/";
    private static final String INVALID_IMAGE = "Некорректное изображение";
    private static final String IMAGE_TOO_LARGE = "Максимальный размер изображения 15 Mb";
    private static final String UNSUPPORTED_FORMAT = "Допустимые форматы изображения: PNG, JPEG, GIF, WEBP";
    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] GIF = {0x47, 0x49, 0x46, 0x38};
    private static final byte[] RIFF = {0x52, 0x49, 0x46, 0x46};
    private static final byte[] WEBP = {0x57, 0x45, 0x42, 0x50};

    public static DecodedPhoto decode(String dataUrl) {
        byte[] bytes = toBytes(dataUrl);
        String contentType = mimeType(bytes);
        if (contentType == null) {
            throw new InvalidImageException(UNSUPPORTED_FORMAT);
        }
        return new DecodedPhoto(bytes, contentType, extension(contentType));
    }

    public static byte[] toBytes(String dataUrl) {
        if (dataUrl == null) {
            throw new InvalidImageException(INVALID_IMAGE);
        }
        int comma = dataUrl.indexOf(',');
        if (comma < 0 || !dataUrl.regionMatches(true, 0, DATA_IMAGE_PREFIX, 0, DATA_IMAGE_PREFIX.length())) {
            throw new InvalidImageException(INVALID_IMAGE);
        }
        String header = dataUrl.substring(0, comma);
        if (!header.contains(";base64")) {
            throw new InvalidImageException(INVALID_IMAGE);
        }
        String payload = dataUrl.substring(comma + 1);
        if (payload.length() > MAX_PAYLOAD_LENGTH || alphabetChars(payload) > MAX_PAYLOAD_CHARS) {
            throw new InvalidImageException(IMAGE_TOO_LARGE);
        }
        byte[] bytes = decodePayload(payload);
        if (bytes.length == 0) {
            throw new InvalidImageException(INVALID_IMAGE);
        }
        if (bytes.length > MAX_PHOTO_BYTES) {
            throw new InvalidImageException(IMAGE_TOO_LARGE);
        }
        if (mimeType(bytes) == null) {
            throw new InvalidImageException(UNSUPPORTED_FORMAT);
        }
        return bytes;
    }

    public static String toDataUrl(byte[] photo) {
        if (photo == null || photo.length == 0) {
            return null;
        }
        String mime = mimeType(photo);
        if (mime == null) {
            mime = "application/octet-stream";
        }
        return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(photo);
    }

    private static byte[] decodePayload(String payload) {
        try {
            return Base64.getMimeDecoder().decode(payload);
        } catch (IllegalArgumentException exception) {
            throw new InvalidImageException(INVALID_IMAGE, exception);
        }
    }

    private static int alphabetChars(String payload) {
        int count = 0;
        for (int i = 0; i < payload.length(); i++) {
            if (!isBase64Char(payload.charAt(i))) {
                continue;
            }
            count++;
            if (count > MAX_PAYLOAD_CHARS) {
                return count;
            }
        }
        return count;
    }

    private static boolean isBase64Char(char c) {
        return (c >= 'A' && c <= 'Z')
                || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9')
                || c == '+'
                || c == '/'
                || c == '=';
    }

    private static String extension(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> throw new InvalidImageException(UNSUPPORTED_FORMAT);
        };
    }

    private static String mimeType(byte[] photo) {
        if (startsWith(photo, PNG)) {
            return "image/png";
        }
        if (startsWith(photo, JPEG)) {
            return "image/jpeg";
        }
        if (startsWith(photo, GIF)) {
            return "image/gif";
        }
        if (startsWith(photo, RIFF) && matches(photo, 8, WEBP)) {
            return "image/webp";
        }
        return null;
    }

    private static boolean startsWith(byte[] data, byte[] signature) {
        return matches(data, 0, signature);
    }

    private static boolean matches(byte[] data, int offset, byte[] signature) {
        int end = offset + signature.length;
        return data.length >= end && Arrays.mismatch(data, offset, end, signature, 0, signature.length) == -1;
    }
}
