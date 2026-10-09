package mipkasnow.dev.rococo.repository;

import mipkasnow.dev.rococo.entity.CountryEntity;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    default CountryEntity findByIdOrThrow(UUID id) {
        return findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CountryNotFound));
    }
}
