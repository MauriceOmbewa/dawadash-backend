package com.maurice.dawadash_backend.global.auth;

import com.maurice.dawadash_backend.global.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /** Angular calls this on startup to learn if someone is logged in, and what their role is. */
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal OidcUser principal) {
        return UserResponse.from(userService.getByEmail(principal.getEmail()));
    }
}