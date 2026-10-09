package mipkasnow.dev.rococo.dto.user;

import mipkasnow.dev.rococo.entity.UserEntity;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String firstname,
        String lastname,
        String avatar
) {

    public UserResponse(UserEntity user, String avatar) {
        this(
                user.getId(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                avatar
        );
    }
}
