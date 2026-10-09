package mipkasnow.dev.rococo.service;

import mipkasnow.dev.rococo.dto.country.CountryDto;
import mipkasnow.dev.rococo.entity.CountryEntity;
import mipkasnow.dev.rococo.repository.CountryRepository;
import mipkasnow.dev.rococo.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CountryService {

    private final List<CountryDto> countries;

    public CountryService(CountryRepository countryRepository) {
        countries = countryRepository.findAll(PageUtil.defaultAscSort(CountryEntity.Fields.name))
                .stream()
                .map(CountryDto::new)
                .toList();
    }

    public PagedModel<CountryDto> getCountries(Pageable pageable) {
        List<CountryDto> content = countries.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();

        Page<CountryDto> page = new PageImpl<>(
                content,
                pageable,
                countries.size()
        );

        return new PagedModel<>(page);
    }
}

