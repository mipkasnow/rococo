package mipkasnow.dev.rococo.repository;

import mipkasnow.dev.rococo.entity.UserEntity;
import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = "avatarFile")
    Optional<UserEntity> findByUsername(String username);

    default UserEntity findByUsernameOrThrow(String username) {
        return findByUsername(username).orElseThrow(() -> new ApiException(ErrorCode.UserNotFound));
    }
}
