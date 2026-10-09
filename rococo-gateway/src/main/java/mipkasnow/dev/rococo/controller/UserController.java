package mipkasnow.dev.rococo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.dto.user.UserResponse;
import mipkasnow.dev.rococo.dto.user.UserUpdateRequest;
import mipkasnow.dev.rococo.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public UserResponse currentUser() {
        return userService.current();
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public UserResponse updateUser(@Valid @RequestBody UserUpdateRequest updateRequest) {
        return userService.update(updateRequest);
    }
}
