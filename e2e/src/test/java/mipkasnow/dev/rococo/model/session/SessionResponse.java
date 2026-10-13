package mipkasnow.dev.rococo.model.session;

public record SessionResponse(
        String username,
        String issuedAt,
        String expiresAt
) {
}
