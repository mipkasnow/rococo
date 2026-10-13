package mipkasnow.dev.rococo.model.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String firstname,
        String lastname,
        String avatar
) {
}
