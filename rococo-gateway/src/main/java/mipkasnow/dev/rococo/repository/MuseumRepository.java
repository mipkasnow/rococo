package mipkasnow.dev.rococo.repository;

import mipkasnow.dev.rococo.entity.MuseumEntity;
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
public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID>, JpaSpecificationExecutor<MuseumEntity> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, UUID id);

    @EntityGraph(attributePaths = {"country", "photoFile"})
    @NonNull
    Optional<MuseumEntity> findById(@NonNull UUID id);

    default MuseumEntity findByIdWithCountryOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new ApiException(ErrorCode.MuseumNotFound));
    }

    @EntityGraph(attributePaths = {"country", "photoFile"})
    @NonNull
    Page<MuseumEntity> findAll(@Nullable Specification<MuseumEntity> spec, @NonNull Pageable pageable);
}
