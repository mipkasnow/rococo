package mipkasnow.dev.rococo.test.rest;

import lombok.experimental.UtilityClass;
import mipkasnow.dev.rococo.utils.DataUrl;

@UtilityClass
class Images {

    static final String BROKEN = "not-an-image";
    static final String UNSUPPORTED = "data:image/png;base64,aGVsbG8=";

    private static final String ARTIST = "images/artist/Aivazovsky.jpg";
    private static final String MUSEUM = "images/museum/Hermitage museum.jpg";
    private static final String PAINTING = "images/painting/Kalitka, Vasnetsov.jpg";

    static String artist() {
        return DataUrl.fromClasspath(ARTIST);
    }

    static String museum() {
        return DataUrl.fromClasspath(MUSEUM);
    }

    static String painting() {
        return DataUrl.fromClasspath(PAINTING);
    }
}
