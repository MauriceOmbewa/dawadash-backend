package com.maurice.dawadash_backend.global.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${app.admin-emails:}")
    private List<String> adminEmails;

    /** Called on every Google login. Existing user -> refresh profile. New user -> create. */
    @Transactional
    public User findOrCreateFromGoogle(String googleId, String email, String name, String pictureUrl) {
        return userRepository.findByGoogleId(googleId)
                .map(existing -> {
                    existing.setName(name);
                    existing.setPictureUrl(pictureUrl);
                    return existing;
                })
                .orElseGet(() -> userRepository.save(createNewUser(googleId, email, name, pictureUrl)));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /** Admin gives an extra role, e.g. a verified customer becomes a DOCTOR too. */
    @Transactional
    public User addRole(Long userId, Role role) {
        User user = findById(userId);
        user.getRoles().add(role);
        return user;
    }

    /** Admin takes a role away. CUSTOMER can never be removed. */
    @Transactional
    public User removeRole(Long userId, Role role) {
        if (role == Role.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every user must keep the CUSTOMER role");
        }
        User user = findById(userId);
        user.getRoles().remove(role);
        return user;
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private User createNewUser(String googleId, String email, String name, String pictureUrl) {
        User user = new User();
        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setName(name);
        user.setPictureUrl(pictureUrl);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER); // everyone is a customer
        if (isAdminEmail(email)) {
            roles.add(Role.ADMIN);
        }
        user.setRoles(roles);
        return user;
    }

    private boolean isAdminEmail(String email) {
        return adminEmails.stream().anyMatch(e -> e.trim().equalsIgnoreCase(email));
    }
}