package mipkasnow.dev.rococo.controller;

import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.session.SessionResponse;
import mipkasnow.dev.rococo.service.SessionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public SessionResponse currentSession(@AuthenticationPrincipal Jwt jwt) {
        return sessionService.current(jwt);
    }
}
