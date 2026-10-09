package mipkasnow.dev.rococo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.dto.artist.ArtistResponse;
import mipkasnow.dev.rococo.dto.artist.ArtistUpdateRequest;
import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.service.ArtistService;
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
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public PagedModel<ArtistResponse> getArtists(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {

        Pageable pageable = PageUtil.createPageableAscSorted(page, size, PageUtil.defaultAscSort(ArtistEntity.Fields.name));
        return artistService.findAll(pageable, name);
    }

    @GetMapping("/{artistId}")
    @PreAuthorize("permitAll()")
    public ArtistResponse getArtist(@PathVariable UUID artistId) {
        return artistService.findById(artistId);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ArtistResponse createArtist(@Valid @RequestBody ArtistCreateRequest createRequest) {
        return artistService.create(createRequest);
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ArtistResponse updateArtist(@Valid @RequestBody ArtistUpdateRequest updateRequest) {
        return artistService.update(updateRequest);
    }
}
