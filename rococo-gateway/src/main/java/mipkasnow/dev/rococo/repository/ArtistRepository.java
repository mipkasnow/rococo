package mipkasnow.dev.rococo.repository;

import mipkasnow.dev.rococo.entity.ArtistEntity;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID>, JpaSpecificationExecutor<ArtistEntity> {

    boolean existsByName(String artistName);

    boolean existsByNameAndIdNot(String artistName, UUID artistId);

    @Override
    @EntityGraph(attributePaths = "photoFile")
    @NonNull
    Optional<ArtistEntity> findById(@NonNull UUID artistId);

    @Override
    @EntityGraph(attributePaths = "photoFile")
    @NonNull
    Page<ArtistEntity> findAll(@Nullable Specification<ArtistEntity> spec, @NonNull Pageable pageable);

    default ArtistEntity findByIdOrThrow(UUID artistId) {
        return findById(artistId).orElseThrow(() -> new ApiException(ErrorCode.ArtistNotFound));
    }
}
