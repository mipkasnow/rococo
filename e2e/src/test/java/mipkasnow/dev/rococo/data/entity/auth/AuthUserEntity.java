package mipkasnow.dev.rococo.data.entity.auth;

import java.util.List;
import java.util.UUID;

public record AuthUserEntity(
        UUID id,
        String username,
        String password,
        boolean enabled,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        List<String> authorities
) {

    public AuthUserEntity withAuthorities(List<String> authorities) {
        return new AuthUserEntity(
                id,
                username,
                password,
                enabled,
                accountNonExpired,
                accountNonLocked,
                credentialsNonExpired,
                List.copyOf(authorities)
        );
    }
}
