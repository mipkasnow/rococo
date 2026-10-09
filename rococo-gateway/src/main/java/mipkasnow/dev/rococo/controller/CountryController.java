package mipkasnow.dev.rococo.controller;

import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.country.CountryDto;
import mipkasnow.dev.rococo.service.CountryService;
import mipkasnow.dev.rococo.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/country")
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public PagedModel<CountryDto> getCountries(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        Pageable pageable = PageUtil.createPageableUnsorted(page, size);
        return countryService.getCountries(pageable);
    }
}
