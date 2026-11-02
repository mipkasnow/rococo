package mipkasnow.dev.rococo.test.web;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import mipkasnow.dev.rococo.api.client.CountryApiClient;
import mipkasnow.dev.rococo.api.client.MuseumApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.Museums;
import mipkasnow.dev.rococo.jupiter.annotation.meta.WebTest;
import mipkasnow.dev.rococo.jupiter.extension.ApiLoginExtension;
import mipkasnow.dev.rococo.model.country.CountryDto;
import mipkasnow.dev.rococo.model.country.CountryRequest;
import mipkasnow.dev.rococo.model.geo.GeoRequest;
import mipkasnow.dev.rococo.model.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.page.MuseumPage;
import mipkasnow.dev.rococo.utils.DataUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;

@WebTest
@Feature("Музеи")
public class MuseumTest {

    private static final String RUSSIAN_MUSEUM = "Русский музей";
    private static final String PHOTO = "images/museum/Hermitage museum.jpg";
    private static final int PAGE_SIZE = 4;

    private MuseumApiClient museumApiClient;
    private CountryApiClient countryApiClient;

    @Test
    @ApiLogin
    @Museums(count = 3)
    @DisplayName("Список музеев совпадает с API, поиск находит Русский музей")
    void museumsShouldMatchApiAndSearch() {
        List<MuseumResponse> museums = museumApiClient.findAll(0, PAGE_SIZE, null).content();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkThatPageLoaded()
                .checkMuseums(museums);

        ensureRussianMuseum();

        new MuseumPage()
                .search(RUSSIAN_MUSEUM)
                .checkFoundTitle(RUSSIAN_MUSEUM);
    }

    private void ensureRussianMuseum() {
        boolean exists = museumApiClient.findAll(0, PAGE_SIZE, RUSSIAN_MUSEUM).content().stream()
                .anyMatch(museum -> RUSSIAN_MUSEUM.equals(museum.title()));
        if (exists) {
            return;
        }
        CountryDto country = countryApiClient.findAll(ApiLoginExtension.token(), 0, 50).content().getFirst();
        museumApiClient.create(new MuseumCreateRequest(
                RUSSIAN_MUSEUM,
                randomString(20),
                DataUrl.fromClasspath(PHOTO),
                new GeoRequest(randomString(8), new CountryRequest(country.id()))
        ));
    }
}
