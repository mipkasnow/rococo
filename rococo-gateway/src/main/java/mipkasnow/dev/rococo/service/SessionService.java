package mipkasnow.dev.rococo.service;

import mipkasnow.dev.rococo.dto.session.SessionResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class SessionService {

    private static final DateTimeFormatter SESSION_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ");

    public SessionResponse current(Jwt jwt) {
        if (jwt == null) {
            return new SessionResponse(null, null, null);
        }
        return new SessionResponse(
                jwt.getSubject(),
                format(jwt.getIssuedAt()),
                format(jwt.getExpiresAt())
        );
    }

    private String format(Instant instant) {
        if (instant == null) {
            return null;
        }
        return SESSION_TIME.format(instant.atZone(ZoneId.systemDefault()));
    }
}
