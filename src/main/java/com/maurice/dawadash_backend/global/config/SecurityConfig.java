package com.maurice.dawadash_backend.global.config;

import com.maurice.dawadash_backend.global.security.GoogleOidcUserService;
import com.maurice.dawadash_backend.global.security.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final GoogleOidcUserService googleOidcUserService;
    private final OAuth2LoginSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // TODO before production: turn CSRF protection back on (needed for cookie sessions)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Open to everyone, no login needed (browse medicines, doctors, nurses)
                        .requestMatchers("/api/public/**", "/error", "/h2-console/**").permitAll()

                        // One section per role. Each role only reaches its own URLs.
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/api/nurse/**").hasRole("NURSE")
                        .requestMatchers("/api/hospital/**").hasRole("HOSPITAL")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Everything else (like /api/auth/me) just needs a logged-in user
                        .anyRequest().authenticated())

                // Google login: the Angular button points to /oauth2/authorization/google
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(info -> info.oidcUserService(googleOidcUserService))
                        .successHandler(successHandler))

                // API calls from a not-logged-in user get 401 instead of a redirect to Google
                .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        request -> request.getRequestURI().startsWith("/api/")))

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()))

                // Needed only so the H2 console page can display inside a frame
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}