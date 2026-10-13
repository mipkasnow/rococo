package mipkasnow.dev.rococo.data.entity.gateway;

import java.util.UUID;

public record UserEntity(
        UUID id,
        String username,
        String firstname,
        String lastname,
        UUID avatarFileId
) {
}
