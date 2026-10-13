package mipkasnow.dev.rococo.utils;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Locale;

@UtilityClass
public class DataUrl {

    @Nonnull
    public static String fromClasspath(String classpath) {
        try (InputStream input = DataUrl.class.getClassLoader().getResourceAsStream(classpath)) {
            if (input == null) {
                throw new IllegalStateException("Файл не найден: " + classpath);
            }
            String mime = classpath.toLowerCase(Locale.ROOT).endsWith(".png") ? "image/png" : "image/jpeg";
            return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(input.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Файл не прочитан: " + classpath, e);
        }
    }
}
