package mipkasnow.dev.rococo.integration.controller;

import mipkasnow.dev.rococo.dto.country.CountryDto;
import mipkasnow.dev.rococo.integration.IntegrationTest;
import mipkasnow.dev.rococo.integration.support.ApiClient;
import mipkasnow.dev.rococo.integration.support.PageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class CountryControllerTest {

    private static final String COUNTRIES = "/api/country";
    private static final int MAX_PAGE_SIZE = 50;

    @Autowired
    private ApiClient api;

    @Test
    void defaultPageMatchesExplicitFirstPage() {
        PageResponse<CountryDto> defaults = countries(COUNTRIES);
        PageResponse<CountryDto> explicit = countries(0, 10);
        List<CountryDto> all = allCountries();

        assertThat(defaults).isEqualTo(explicit);
        assertThat(defaults.page().number()).isZero();
        assertThat(defaults.page().size()).isEqualTo(10);
        assertThat(defaults.page().totalElements()).isEqualTo(all.size());
        assertThat(defaults.page().totalPages()).isEqualTo(Math.ceilDiv(all.size(), 10));
        assertThat(defaults.content()).containsExactlyElementsOf(all.subList(0, 10));
    }

    @Test
    void pageAndSizeSliceTheList() {
        List<CountryDto> all = allCountries();
        PageResponse<CountryDto> page = countries(1, 2);

        assertThat(page.page().number()).isEqualTo(1);
        assertThat(page.page().size()).isEqualTo(2);
        assertThat(page.content()).containsExactlyElementsOf(all.subList(2, 4));
    }

    @Test
    void sizeIsCapped() {
        PageResponse<CountryDto> page = countries(0, 100);

        assertThat(page.page().size()).isEqualTo(MAX_PAGE_SIZE);
        assertThat(page.content()).hasSize(MAX_PAGE_SIZE);
    }

    @Test
    void negativePageAndNonPositiveSizeAreNormalized() {
        PageResponse<CountryDto> page = countries(-1, 0);

        assertThat(page.page().number()).isZero();
        assertThat(page.page().size()).isEqualTo(1);
        assertThat(page.content()).containsExactly(allCountries().getFirst());
    }

    @Test
    void pagePastTheEndIsEmpty() {
        long totalElements = countries(0, 10).page().totalElements();
        long pastTheEnd = Math.ceilDiv(totalElements, 10);
        PageResponse<CountryDto> page = countries(pastTheEnd, 10);

        assertThat(page.content()).isEmpty();
        assertThat(page.page().totalElements()).isEqualTo(totalElements);
    }

    @Test
    void seededCountriesAreReturned() {
        List<CountryDto> all = allCountries();

        assertThat(all).extracting(CountryDto::id).doesNotContainNull();
        assertThat(all).extracting(CountryDto::name).doesNotContainNull().contains("Россия", "Франция", "Япония");
    }

    private PageResponse<CountryDto> countries(String uri) {
        return api.anonymous().get(uri, api.pageOf(CountryDto.class));
    }

    private PageResponse<CountryDto> countries(long page, long size) {
        return countries(COUNTRIES + "?page=" + page + "&size=" + size);
    }

    private List<CountryDto> allCountries() {
        PageResponse<CountryDto> first = countries(0, MAX_PAGE_SIZE);
        List<CountryDto> all = new ArrayList<>(first.content());
        for (long page = 1; page < first.page().totalPages(); page++) {
            all.addAll(countries(page, MAX_PAGE_SIZE).content());
        }
        return all;
    }
}
