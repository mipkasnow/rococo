package mipkasnow.dev.rococo.jupiter.extension;

import mipkasnow.dev.rococo.api.client.CountryApiClient;
import mipkasnow.dev.rococo.api.client.MuseumApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.Museums;
import mipkasnow.dev.rococo.model.country.CountryDto;
import mipkasnow.dev.rococo.model.country.CountryRequest;
import mipkasnow.dev.rococo.model.geo.GeoRequest;
import mipkasnow.dev.rococo.model.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.utils.DataUrl;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;

@ParametersAreNonnullByDefault
public class MuseumsExtension implements BeforeEachCallback {

    private static final String PHOTO = "images/museum/Hermitage museum.jpg";
    private static final int COUNTRY_PAGE_SIZE = 50;

    private final MuseumApiClient museumApiClient = new MuseumApiClient();
    private final CountryApiClient countryApiClient = new CountryApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museums.class)
                .ifPresent(this::createMuseums);
    }

    private void createMuseums(Museums museums) {
        int count = museums.count();
        if (count < 1 || count > 3) {
            throw new IllegalArgumentException("Количество музеев должно быть от 1 до 3");
        }
        CountryDto country = countryApiClient.findAll(ApiLoginExtension.token(), 0, COUNTRY_PAGE_SIZE)
                .content()
                .getFirst();
        for (int i = 0; i < count; i++) {
            museumApiClient.create(new MuseumCreateRequest(
                    randomString(12),
                    randomString(20),
                    DataUrl.fromClasspath(PHOTO),
                    new GeoRequest(randomString(8), new CountryRequest(country.id()))
            ));
        }
    }
}
