package com.maurice.dawadash_backend.global.security;

import com.maurice.dawadash_backend.global.user.User;
import com.maurice.dawadash_backend.global.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Runs right after Google confirms who the person is.
 * 1. Find or create the user in OUR database.
 * 2. Give them one authority per role (ROLE_CUSTOMER, ROLE_DOCTOR, ...).
 */
@Service
@RequiredArgsConstructor
public class GoogleOidcUserService extends OidcUserService {

    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser googleUser = super.loadUser(request);

        if (!Boolean.TRUE.equals(googleUser.getEmailVerified())) {
            throw new OAuth2AuthenticationException(new OAuth2Error("email_not_verified"));
        }

        User user = userService.findOrCreateFromGoogle(
                googleUser.getSubject(),
                googleUser.getEmail(),
                googleUser.getFullName(),
                googleUser.getPicture());

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();

        return new DefaultOidcUser(authorities, googleUser.getIdToken(), googleUser.getUserInfo());
    }
}