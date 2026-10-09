package mipkasnow.dev.rococo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.dto.museum.MuseumResponse;
import mipkasnow.dev.rococo.dto.museum.MuseumUpdateRequest;
import mipkasnow.dev.rococo.entity.MuseumEntity;
import mipkasnow.dev.rococo.service.MuseumService;
import mipkasnow.dev.rococo.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/museum")
public class MuseumController {

    private final MuseumService museumService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public PagedModel<MuseumResponse> getMuseums(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String title) {
        Pageable pageable = PageUtil.createPageableAscSorted(page, size, PageUtil.defaultAscSort(MuseumEntity.Fields.title));
        return museumService.findAll(pageable, title);
    }

    @GetMapping("/{museumId}")
    @PreAuthorize("permitAll()")
    public MuseumResponse getMuseum(@PathVariable UUID museumId) {
        return museumService.findById(museumId);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public MuseumResponse createMuseum(@Valid @RequestBody MuseumCreateRequest createRequest) {
        return museumService.create(createRequest);
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public MuseumResponse updateMuseum(@Valid @RequestBody MuseumUpdateRequest updateRequest) {
        return museumService.update(updateRequest);
    }
}
