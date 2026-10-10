package mipkasnow.dev.rococo.integration.support;

import mipkasnow.dev.rococo.util.PhotoCodecUtil;

import java.io.IOException;
import java.io.InputStream;

public final class TestPhotos {

    private TestPhotos() {
    }

    public static String dataUrl(String classpathLocation) {
        try (InputStream input = TestPhotos.class.getClassLoader().getResourceAsStream(classpathLocation)) {
            if (input == null) {
                throw new IllegalStateException("Missing test photo " + classpathLocation);
            }
            return PhotoCodecUtil.toDataUrl(input.readAllBytes());
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
