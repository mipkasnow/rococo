package mipkasnow.dev.rococo.dto.file;

public record DecodedPhoto(byte[] bytes, String contentType, String extension) {
}
