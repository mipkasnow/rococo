package mipkasnow.dev.rococo.test.rest;

import mipkasnow.dev.rococo.api.client.CountryApiClient;
import mipkasnow.dev.rococo.data.entity.gateway.CountryEntity;
import mipkasnow.dev.rococo.data.repository.gateway.CountryRepository;
import mipkasnow.dev.rococo.jupiter.annotation.meta.RestTest;
import mipkasnow.dev.rococo.model.PageResponse;
import mipkasnow.dev.rococo.model.country.CountryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTest
public class CountryRestTest {

    private static final int PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final CountryRepository countryRepository = new CountryRepository();

    private CountryApiClient countryApiClient;
    private List<CountryEntity> countries;

    @BeforeEach
    void loadCountries() {
        countries = countryRepository.findAll();
        assertTrue(countries.size() > MAX_PAGE_SIZE, () -> "В country записей: " + countries.size());
    }

    @Test
    @DisplayName("Первая страница стран совпадает с базой")
    void firstPageShouldMatchDatabase() {
        assertSlice(countryApiClient.findAll(null, 0, PAGE_SIZE), 0, PAGE_SIZE, 0, PAGE_SIZE);
    }

    @Test
    @DisplayName("Вторая страница стран совпадает с базой")
    void secondPageShouldMatchDatabase() {
        assertSlice(countryApiClient.findAll(null, 1, PAGE_SIZE), 1, PAGE_SIZE, PAGE_SIZE, PAGE_SIZE);
    }

    @Test
    @DisplayName("Последняя страница стран совпадает с базой")
    void lastPageShouldMatchDatabase() {
        long totalPages = pages(countries.size(), PAGE_SIZE);
        int last = (int) totalPages - 1;
        int offset = last * PAGE_SIZE;
        assertSlice(
                countryApiClient.findAll(null, last, PAGE_SIZE),
                last,
                PAGE_SIZE,
                offset,
                countries.size() - offset
        );
    }

    @Test
    @DisplayName("Страница за последней пустая, счётчики как в базе")
    void pagePastTheEndShouldBeEmpty() {
        long totalPages = pages(countries.size(), PAGE_SIZE);
        assertSlice(
                countryApiClient.findAll(null, (int) totalPages, PAGE_SIZE),
                totalPages,
                PAGE_SIZE,
                countries.size(),
                0
        );
    }

    @Test
    @DisplayName("Размер страницы больше 50 обрезается до 50")
    void sizeAboveLimitShouldClamp() {
        assertSlice(countryApiClient.findAll(null, 0, MAX_PAGE_SIZE + 1), 0, MAX_PAGE_SIZE, 0, MAX_PAGE_SIZE);
    }

    @Test
    @DisplayName("Отрицательная страница читается как первая")
    void negativePageShouldResetToFirst() {
        assertSlice(countryApiClient.findAll(null, -1, PAGE_SIZE), 0, PAGE_SIZE, 0, PAGE_SIZE);
    }

    @Test
    @DisplayName("Неположительный размер страницы читается как один элемент")
    void nonPositiveSizeShouldBecomeOne() {
        assertSlice(countryApiClient.findAll(null, 0, 0), 0, 1, 0, 1);
        assertSlice(countryApiClient.findAll(null, -1, 0), 0, 1, 0, 1);
        assertSlice(countryApiClient.findAll(null, 0, -5), 0, 1, 0, 1);
    }

    private void assertSlice(PageResponse<CountryDto> page, long number, long size, int offset, int count) {
        assertEquals(number, page.page().number());
        assertEquals(size, page.page().size());
        assertEquals(countries.size(), page.page().totalElements());
        assertEquals(pages(countries.size(), size), page.page().totalPages());
        assertEquals(slice(offset, count), page.content());
    }

    private List<CountryDto> slice(int offset, int count) {
        int from = Math.min(Math.max(offset, 0), countries.size());
        int to = Math.min(from + count, countries.size());
        return countries.subList(from, to).stream()
                .map(country -> new CountryDto(country.id(), country.name()))
                .toList();
    }

    private static long pages(int total, long size) {
        return (total + size - 1) / size;
    }
}
