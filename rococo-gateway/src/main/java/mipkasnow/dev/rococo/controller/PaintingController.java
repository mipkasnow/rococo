package mipkasnow.dev.rococo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.painting.PaintingCreateRequest;
import mipkasnow.dev.rococo.dto.painting.PaintingResponse;
import mipkasnow.dev.rococo.dto.painting.PaintingUpdateRequest;
import mipkasnow.dev.rococo.entity.PaintingEntity;
import mipkasnow.dev.rococo.service.PaintingService;
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
@RequestMapping("/api/painting")
public class PaintingController {

    private final PaintingService paintingService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public PagedModel<PaintingResponse> getPaintings(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String title) {

        Pageable pageable = PageUtil.createPageableAscSorted(page, size, PageUtil.defaultAscSort(PaintingEntity.Fields.title));
        return paintingService.findAll(pageable, title);
    }

    @GetMapping("/author/{artistId}")
    @PreAuthorize("permitAll()")
    public PagedModel<PaintingResponse> getPaintingsByArtist(
            @PathVariable UUID artistId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "9") Integer size) {

        Pageable pageable = PageUtil.createPageableAscSorted(page, size, PageUtil.defaultAscSort(PaintingEntity.Fields.title));
        return paintingService.findAllByArtist(pageable, artistId);
    }

    @GetMapping("/{paintingId}")
    @PreAuthorize("permitAll()")
    public PaintingResponse getPainting(@PathVariable UUID paintingId) {
        return paintingService.findById(paintingId);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public PaintingResponse createPainting(@Valid @RequestBody PaintingCreateRequest createRequest) {
        return paintingService.create(createRequest);
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public PaintingResponse updatePainting(@Valid @RequestBody PaintingUpdateRequest updateRequest) {
        return paintingService.update(updateRequest);
    }
}
