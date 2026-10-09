package mipkasnow.dev.rococo.dto.session;

public record SessionResponse(
        String username,
        String issuedAt,
        String expiresAt
) {
}
