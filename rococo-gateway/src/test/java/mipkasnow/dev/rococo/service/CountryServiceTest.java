package mipkasnow.dev.rococo.service;

import mipkasnow.dev.rococo.dto.country.CountryDto;
import mipkasnow.dev.rococo.entity.CountryEntity;
import mipkasnow.dev.rococo.junit.UnitTest;
import mipkasnow.dev.rococo.repository.CountryRepository;
import mipkasnow.dev.rococo.util.PageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    private List<CountryEntity> countryEntities;
    private CountryService countryService;

    @BeforeEach
    public void setup() {
        countryEntities = getCountryEntities();
        when(countryRepository.findAll(PageUtil.defaultAscSort(CountryEntity.Fields.name))).thenReturn(countryEntities);
        countryService = new CountryService(countryRepository);
    }

    @Test
    void getCountry_shouldReturnListOfCountries_whenPageNumberIs0AndPageSizeIs5() {
        Pageable pageable = PageRequest.of(0, 5);

        PagedModel<CountryDto> countries = countryService.getCountries(pageable);

        assertEquals(pageable.getPageSize(), countries.getMetadata().size());
        assertEquals(pageable.getPageNumber(), countries.getMetadata().number());
        assertEquals(4, countries.getMetadata().totalPages());
        assertEquals(countryEntities.size(), countries.getMetadata().totalElements());

        assertCountries(
                countries.getContent(),
                countryEntities.stream().limit(pageable.getPageSize()).toList()
        );
    }

    @Test
    void getCountry_shouldReturnOneElement_whenPageNumberIs0AndPageSizeIs1() {
        Pageable pageable = PageRequest.of(0, 1);

        PagedModel<CountryDto> countries = countryService.getCountries(pageable);

        assertEquals(pageable.getPageSize(), countries.getMetadata().size());
        assertEquals(pageable.getPageNumber(), countries.getMetadata().number());
        assertEquals(20, countries.getMetadata().totalPages());
        assertEquals(countryEntities.size(), countries.getMetadata().totalElements());

        assertCountries(
                countries.getContent(),
                countryEntities.stream().limit(pageable.getPageSize()).toList()
        );
    }

    private void assertCountries(List<CountryDto> actual, List<CountryEntity> expected) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).id());
            assertEquals(expected.get(i).getName(), actual.get(i).name());
        }
    }

    private List<CountryEntity> getCountryEntities() {
        List<String> names = List.of(
                "Австралия", "Бразилия", "Великобритания", "Германия", "Египет",
                "Индия", "Испания", "Италия", "Канада", "Китай",
                "Мексика", "Норвегия", "Россия", "США", "Турция",
                "Франция", "Швейцария", "Швеция", "Южная Корея", "Япония"
        );

        return names.stream()
                .map(name -> new CountryEntity(UUID.randomUUID(), name))
                .toList();
    }
}