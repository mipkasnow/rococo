package mipkasnow.dev.rococo.utils;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;

@UtilityClass
public class RandomDataUtils {

    private static final Faker faker = new Faker();

    @Nonnull
    public static String randomUsername() {
        String username = faker.name().username().replaceAll("\\s", "");
        if (username.length() > 50) {
            username = username.substring(0, 50);
        }
        if (username.length() < 3) {
            username = username + "e2e";
        }
        return username;
    }

    @Nonnull
    public static String randomPassword() {
        return faker.internet().password(8, 12, true, false, true);
    }

    @Nonnull
    public static String randomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("String length must be positive: " + length);
        }
        return faker.lorem().characters(length);
    }
}
