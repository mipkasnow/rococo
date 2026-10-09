package mipkasnow.dev.rococo.service;

import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.dto.artist.ArtistResponse;
import mipkasnow.dev.rococo.dto.artist.ArtistUpdateRequest;
import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.entity.FileEntity;
import mipkasnow.dev.rococo.entity.MinioBucket;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import mipkasnow.dev.rococo.repository.ArtistRepository;
import mipkasnow.dev.rococo.repository.specification.ArtistSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final S3FileService s3FileService;

    @Transactional
    public ArtistResponse create(ArtistCreateRequest createRequest) {
        if (artistRepository.existsByName(createRequest.name())) {
            throw new ApiException(ErrorCode.ArtistNameTaken);
        }

        FileEntity photo = s3FileService.save(MinioBucket.artist, null, createRequest.photo());
        ArtistEntity createdArtist = artistRepository.save(new ArtistEntity(createRequest, photo));
        return toResponse(createdArtist);
    }

    @Transactional
    public ArtistResponse update(ArtistUpdateRequest updateRequest) {
        ArtistEntity foundArtist = artistRepository.findByIdOrThrow(updateRequest.id());

        if (artistRepository.existsByNameAndIdNot(updateRequest.name(), updateRequest.id())) {
            throw new ApiException(ErrorCode.ArtistNameTaken);
        }

        FileEntity photo = s3FileService.save(MinioBucket.artist, foundArtist.getPhotoFile(), updateRequest.photo());
        foundArtist.update(updateRequest, photo);
        ArtistEntity updatedArtist = artistRepository.save(foundArtist);
        return toResponse(updatedArtist);
    }

    @Transactional(readOnly = true)
    public ArtistResponse findById(UUID artistId) {
        ArtistEntity foundArtist = artistRepository.findByIdOrThrow(artistId);
        return toResponse(foundArtist);
    }

    @Transactional(readOnly = true)
    public PagedModel<ArtistResponse> findAll(Pageable pageable, String artistName) {
        Page<ArtistResponse> pageOfArtists = artistRepository.findAll(ArtistSpecification.filter(artistName), pageable)
                .map(this::toResponse);
        return new PagedModel<>(pageOfArtists);
    }

    private ArtistResponse toResponse(ArtistEntity artist) {
        return new ArtistResponse(artist, s3FileService.toDataUrl(artist.getPhotoFile()));
    }
}
