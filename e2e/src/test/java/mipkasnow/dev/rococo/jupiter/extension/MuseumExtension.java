package mipkasnow.dev.rococo.jupiter.extension;

import mipkasnow.dev.rococo.api.client.CountryApiClient;
import mipkasnow.dev.rococo.api.client.MuseumApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.Museum;
import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.country.CountryDto;
import mipkasnow.dev.rococo.model.country.CountryRequest;
import mipkasnow.dev.rococo.model.geo.GeoRequest;
import mipkasnow.dev.rococo.model.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.utils.DataUrl;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;

@ParametersAreNonnullByDefault
public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);

    private static final String PHOTO = "images/museum/Hermitage museum.jpg";
    private static final int COUNTRY_PAGE_SIZE = 50;

    private final MuseumApiClient museumApiClient = new MuseumApiClient();
    private final CountryApiClient countryApiClient = new CountryApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(museum -> {
                    CountryDto country = country(museum.country());
                    MuseumResponse created = museumApiClient.create(new MuseumCreateRequest(
                            value(museum.title(), 12),
                            value(museum.description(), 20),
                            DataUrl.fromClasspath(PHOTO),
                            new GeoRequest(value(museum.city(), 8), new CountryRequest(country.id()))
                    ));
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Nonnull
    public static MuseumResponse museum() {
        ExtensionContext context = TestMethodContextExtension.context();
        MuseumResponse museum = context.getStore(NAMESPACE).get(context.getUniqueId(), MuseumResponse.class);
        if (museum == null) {
            throw new IllegalStateException("@Museum is required");
        }
        return museum;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(MuseumResponse.class);
    }

    @Override
    @Nonnull
    public MuseumResponse resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return museum();
    }

    @Nonnull
    private CountryDto country(String requested) {
        String token = ApiLoginExtension.token();
        if (requested.isBlank()) {
            return countryApiClient.findAll(token, 0, COUNTRY_PAGE_SIZE).content().getFirst();
        }
        for (int page = 0; page < 20; page++) {
            PageResponse<CountryDto> response = countryApiClient.findAll(token, page, COUNTRY_PAGE_SIZE);
            for (CountryDto country : response.content()) {
                if (requested.equals(country.name())) {
                    return country;
                }
            }
            if (page + 1 >= response.page().totalPages()) {
                break;
            }
        }
        throw new IllegalStateException("Страна не найдена: " + requested);
    }

    @Nonnull
    private static String value(String requested, int length) {
        return requested.isBlank() ? randomString(length) : requested;
    }
}
