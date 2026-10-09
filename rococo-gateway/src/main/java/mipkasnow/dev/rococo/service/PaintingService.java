package mipkasnow.dev.rococo.service;

import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.painting.PaintingCreateRequest;
import mipkasnow.dev.rococo.dto.painting.PaintingResponse;
import mipkasnow.dev.rococo.dto.painting.PaintingUpdateRequest;
import mipkasnow.dev.rococo.dto.artist.ArtistResponse;
import mipkasnow.dev.rococo.dto.museum.MuseumResponse;
import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.entity.FileEntity;
import mipkasnow.dev.rococo.entity.MinioBucket;
import mipkasnow.dev.rococo.entity.MuseumEntity;
import mipkasnow.dev.rococo.entity.PaintingEntity;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import mipkasnow.dev.rococo.repository.ArtistRepository;
import mipkasnow.dev.rococo.repository.MuseumRepository;
import mipkasnow.dev.rococo.repository.PaintingRepository;
import mipkasnow.dev.rococo.repository.specification.PaintingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final ArtistRepository artistRepository;
    private final MuseumRepository museumRepository;
    private final S3FileService s3FileService;

    @Transactional
    public PaintingResponse create(PaintingCreateRequest createRequest) {
        if (paintingRepository.existsByTitle(createRequest.title())) {
            throw new ApiException(ErrorCode.PaintingTitleTaken);
        }

        ArtistEntity foundArtist = artistRepository.findByIdOrThrow(createRequest.getArtistId());
        MuseumEntity foundMuseum = resolveMuseum(createRequest.getMuseumId(), null);
        FileEntity content = s3FileService.save(MinioBucket.painting, null, createRequest.content());

        PaintingEntity createdPainting = paintingRepository.save(new PaintingEntity(createRequest, foundArtist, foundMuseum, content));

        return toResponse(createdPainting);
    }

    @Transactional
    public PaintingResponse update(PaintingUpdateRequest updateRequest) {
        PaintingEntity foundPainting = paintingRepository.findByIdWithAllRelatedEntitiesOrThrow(updateRequest.id());

        if (paintingRepository.existsByTitleAndIdNot(updateRequest.title(), updateRequest.id())) {
            throw new ApiException(ErrorCode.PaintingTitleTaken);
        }

        ArtistEntity artistToUpdate = resolveArtist(updateRequest.getArtistId(), foundPainting.getArtist());
        MuseumEntity museumToUpdate = resolveMuseum(updateRequest.getMuseumId(), foundPainting.getMuseum());
        FileEntity content = s3FileService.save(MinioBucket.painting, foundPainting.getContentFile(), updateRequest.content());

        foundPainting.update(updateRequest, artistToUpdate, museumToUpdate, content);

        PaintingEntity updatedPainting = paintingRepository.save(foundPainting);

        return toResponse(updatedPainting);
    }

    @Transactional(readOnly = true)
    public PaintingResponse findById(UUID paintingId) {
        PaintingEntity foundPainting = paintingRepository.findByIdWithAllRelatedEntitiesOrThrow(paintingId);
        return toResponse(foundPainting);
    }

    @Transactional(readOnly = true)
    public PagedModel<PaintingResponse> findAll(Pageable pageable, String paintingTitle) {
        return findAll(pageable, paintingTitle, null);
    }

    @Transactional(readOnly = true)
    public PagedModel<PaintingResponse> findAllByArtist(Pageable pageable, UUID artistId) {
        return findAll(pageable, null, artistId);
    }

    private PagedModel<PaintingResponse> findAll(Pageable pageable, String title, UUID artistId) {
        Page<PaintingResponse> pageOfPaintings = paintingRepository.findAll(PaintingSpecification.filter(title, artistId), pageable)
                .map(this::toResponse);
        return new PagedModel<>(pageOfPaintings);
    }

    private PaintingResponse toResponse(PaintingEntity painting) {
        ArtistEntity artist = painting.getArtist();
        MuseumEntity museum = painting.getMuseum();
        MuseumResponse museumResponse = museum == null
                ? null
                : new MuseumResponse(museum, s3FileService.toDataUrl(museum.getPhotoFile()));
        ArtistResponse artistResponse = new ArtistResponse(artist, s3FileService.toDataUrl(artist.getPhotoFile()));
        return new PaintingResponse(
                painting.getId(),
                painting.getTitle(),
                painting.getDescription(),
                s3FileService.toDataUrl(painting.getContentFile()),
                museumResponse,
                artistResponse
        );
    }

    private ArtistEntity resolveArtist(UUID requestedArtistId, ArtistEntity currentArtist) {
        if (ObjectUtils.nullSafeEquals(requestedArtistId, currentArtist.getId())) {
            return currentArtist;
        }
        return artistRepository.findByIdOrThrow(requestedArtistId);
    }

    private MuseumEntity resolveMuseum(UUID requestedMuseumId, MuseumEntity currentMuseum) {
        if (requestedMuseumId == null) {
            return null;
        }
        if (currentMuseum != null && requestedMuseumId.equals(currentMuseum.getId())) {
            return currentMuseum;
        }
        return museumRepository.findByIdWithCountryOrThrow(requestedMuseumId);
    }
}
