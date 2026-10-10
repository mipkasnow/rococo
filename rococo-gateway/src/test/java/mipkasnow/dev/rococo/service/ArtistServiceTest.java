package mipkasnow.dev.rococo.service;

import mipkasnow.dev.rococo.dto.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.dto.artist.ArtistResponse;
import mipkasnow.dev.rococo.dto.artist.ArtistUpdateRequest;
import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.entity.FileEntity;
import mipkasnow.dev.rococo.entity.MinioBucket;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import mipkasnow.dev.rococo.junit.MockLogger;
import mipkasnow.dev.rococo.junit.UnitTest;
import mipkasnow.dev.rococo.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.UUID;

import static mipkasnow.dev.rococo.helper.AssertUtil.assertApiException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class ArtistServiceTest {

    @MockLogger
    static Logger log;

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private S3FileService s3FileService;
    @InjectMocks
    private ArtistService artistService;

    private final UUID artistId = UUID.randomUUID();
    private final String name = "name";
    private final String biography = "biography";
    private final String photo = "photo";

    private final ArtistCreateRequest createRequest = new ArtistCreateRequest(name, biography, photo);

    @Test
    void create_shouldCreateArtist_whenNameIsFree() {
        FileEntity photoFile = new FileEntity();
        ArtistEntity created = new ArtistEntity(createRequest, photoFile);
        created.setId(artistId);

        when(artistRepository.existsByName(name)).thenReturn(false);
        when(s3FileService.save(MinioBucket.artist, null, photo)).thenReturn(photoFile);
        when(artistRepository.save(new ArtistEntity(createRequest, photoFile))).thenReturn(created);
        when(s3FileService.toDataUrl(photoFile)).thenReturn(photo);

        ArtistResponse response = artistService.create(createRequest);

        assertEquals(response.id(), artistId);
        assertEquals(response.name(), name);
        assertEquals(response.biography(), biography);
        assertEquals(response.photo(), photo);
        verify(log).info("Created artist {}", created);
    }

    @Test
    void create_shouldThrowException_whenNameIsTaken() {
        when(artistRepository.existsByName(name)).thenReturn(true);

        assertApiException(ErrorCode.ArtistNameTaken, () -> artistService.create(createRequest));
    }

    @Test
    void update_shouldUpdateArtist_whenRequestIsValid() {
        FileEntity previousPhoto = new FileEntity();
        FileEntity savedPhoto = new FileEntity();
        ArtistEntity artist = artist(previousPhoto);
        String newName = "new name";
        String newBiography = "new biography";
        ArtistUpdateRequest request = new ArtistUpdateRequest(artistId, newName, newBiography, photo);

        when(artistRepository.findByIdOrThrow(artistId)).thenReturn(artist);
        when(artistRepository.existsByNameAndIdNot(newName, artistId)).thenReturn(false);
        when(s3FileService.save(MinioBucket.artist, previousPhoto, photo)).thenReturn(savedPhoto);
        when(artistRepository.save(artist)).thenReturn(artist);
        when(s3FileService.toDataUrl(savedPhoto)).thenReturn(photo);

        ArtistResponse response = artistService.update(request);

        assertEquals(response.id(), artistId);
        assertEquals(newName, response.name());
        assertEquals(newBiography, response.biography());
        assertEquals(response.photo(), photo);
        verify(log).info("Updated artist {}", artist);
    }

    @Test
    void update_shouldThrowException_whenArtistNotFound() {
        when(artistRepository.findByIdOrThrow(artistId)).thenThrow(new ApiException(ErrorCode.ArtistNotFound));

        assertApiException(
                ErrorCode.ArtistNotFound,
                () -> artistService.update(new ArtistUpdateRequest(artistId, name, biography, photo))
        );
    }

    @Test
    void update_shouldThrowException_whenNameIsTaken() {
        when(artistRepository.findByIdOrThrow(artistId)).thenReturn(artist(new FileEntity()));
        when(artistRepository.existsByNameAndIdNot(name, artistId)).thenReturn(true);

        assertApiException(
                ErrorCode.ArtistNameTaken,
                () -> artistService.update(new ArtistUpdateRequest(artistId, name, biography, photo))
        );
    }

    @Test
    void findById_shouldReturnArtist_whenArtistExists() {
        FileEntity photoFile = new FileEntity();

        when(artistRepository.findByIdOrThrow(artistId)).thenReturn(artist(photoFile));
        when(s3FileService.toDataUrl(photoFile)).thenReturn(photo);

        ArtistResponse response = artistService.findById(artistId);

        assertEquals(response.id(), artistId);
        assertEquals(response.name(), name);
        assertEquals(response.biography(), biography);
        assertEquals(response.photo(), photo);
    }

    @Test
    void findById_shouldThrowException_whenArtistNotFound() {
        when(artistRepository.findByIdOrThrow(artistId)).thenThrow(new ApiException(ErrorCode.ArtistNotFound));

        assertApiException(ErrorCode.ArtistNotFound, () -> artistService.findById(artistId));
    }

    @Test
    void findAll_shouldReturnArtists_whenPageIsRequested() {
        Pageable pageable = PageRequest.of(0, 5);
        FileEntity photoFile = new FileEntity();
        ArtistEntity artist = artist(photoFile);

        when(artistRepository.findAll(ArgumentMatchers.<Specification<ArtistEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(artist), pageable, 1));
        when(s3FileService.toDataUrl(photoFile)).thenReturn(photo);

        PagedModel<ArtistResponse> artists = artistService.findAll(pageable, name);

        assertEquals(pageable.getPageSize(), artists.getMetadata().size());
        assertEquals(pageable.getPageNumber(), artists.getMetadata().number());
        assertEquals(1L, artists.getMetadata().totalElements());
        assertEquals(1L, artists.getMetadata().totalPages());
        assertEquals(1, artists.getContent().size());
        assertEquals(artists.getContent().getFirst().id(), artistId);
        assertEquals(artists.getContent().getFirst().name(), name);
        assertEquals(artists.getContent().getFirst().biography(), biography);
        assertEquals(artists.getContent().getFirst().photo(), photo);
    }

    private ArtistEntity artist(FileEntity photoFile) {
        ArtistEntity artist = new ArtistEntity(createRequest, photoFile);
        artist.setId(artistId);
        return artist;
    }
}
