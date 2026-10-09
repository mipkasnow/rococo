package mipkasnow.dev.rococo.service;

import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.museum.MuseumCreateRequest;
import mipkasnow.dev.rococo.dto.museum.MuseumResponse;
import mipkasnow.dev.rococo.dto.museum.MuseumUpdateRequest;
import mipkasnow.dev.rococo.entity.CountryEntity;
import mipkasnow.dev.rococo.entity.FileEntity;
import mipkasnow.dev.rococo.entity.MinioBucket;
import mipkasnow.dev.rococo.entity.MuseumEntity;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import mipkasnow.dev.rococo.repository.CountryRepository;
import mipkasnow.dev.rococo.repository.MuseumRepository;
import mipkasnow.dev.rococo.repository.specification.MuseumSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MuseumService {

    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;
    private final S3FileService s3FileService;

    @Transactional
    public MuseumResponse create(MuseumCreateRequest createRequest) {
        CountryEntity foundCountry = countryRepository.findByIdOrThrow(createRequest.getCountryId());

        if (museumRepository.existsByTitle(createRequest.title())) {
            throw new ApiException(ErrorCode.MuseumTitleTaken);
        }

        FileEntity photo = s3FileService.save(MinioBucket.museum, null, createRequest.photo());
        MuseumEntity createdMuseum = museumRepository.save(new MuseumEntity(createRequest, foundCountry, photo));
        return toResponse(createdMuseum);
    }

    @Transactional
    public MuseumResponse update(MuseumUpdateRequest updateRequest) {
        MuseumEntity foundMuseum = museumRepository.findByIdWithCountryOrThrow(updateRequest.id());
        CountryEntity countryToUpdate = resolveCountry(updateRequest.getCountryId(), foundMuseum.getCountry());

        if (museumRepository.existsByTitleAndIdNot(updateRequest.title(), updateRequest.id())) {
            throw new ApiException(ErrorCode.MuseumTitleTaken);
        }

        FileEntity photo = s3FileService.save(MinioBucket.museum, foundMuseum.getPhotoFile(), updateRequest.photo());
        foundMuseum.update(updateRequest, countryToUpdate, photo);
        MuseumEntity updatedMuseum = museumRepository.save(foundMuseum);
        return toResponse(updatedMuseum);
    }

    @Transactional(readOnly = true)
    public MuseumResponse findById(UUID museumId) {
        MuseumEntity foundMuseum = museumRepository.findByIdWithCountryOrThrow(museumId);
        return toResponse(foundMuseum);
    }

    @Transactional(readOnly = true)
    public PagedModel<MuseumResponse> findAll(Pageable pageable, String museumTitle) {
        Page<MuseumResponse> pageOfMuseums = museumRepository.findAll(MuseumSpecification.filter(museumTitle), pageable)
                .map(this::toResponse);
        return new PagedModel<>(pageOfMuseums);
    }

    private MuseumResponse toResponse(MuseumEntity museum) {
        return new MuseumResponse(museum, s3FileService.toDataUrl(museum.getPhotoFile()));
    }

    private CountryEntity resolveCountry(UUID requestedCountryId, CountryEntity currentCountry) {
        if (ObjectUtils.nullSafeEquals(requestedCountryId, currentCountry.getId())) {
            return currentCountry;
        }
        return countryRepository.findByIdOrThrow(requestedCountryId);
    }
}
