package mipkasnow.dev.rococo.repository;

import mipkasnow.dev.rococo.entity.PaintingEntity;
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
public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID>, JpaSpecificationExecutor<PaintingEntity> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, UUID id);

    @EntityGraph(attributePaths = {"contentFile", "artist", "artist.photoFile", "museum", "museum.country", "museum.photoFile"})
    @NonNull
    Optional<PaintingEntity> findById(@NonNull UUID paintingId);

    @EntityGraph(attributePaths = {"contentFile", "artist", "artist.photoFile", "museum", "museum.country", "museum.photoFile"})
    @NonNull
    Page<PaintingEntity> findAll(@Nullable Specification<PaintingEntity> spec, @NonNull Pageable pageable);

    default PaintingEntity findByIdWithAllRelatedEntitiesOrThrow(UUID paintingId) {
        return findById(paintingId).orElseThrow(() -> new ApiException(ErrorCode.PaintingNotFound));
    }
}
