package com.maurice.dawadash_backend.global.auth;

import com.maurice.dawadash_backend.global.user.Role;
import com.maurice.dawadash_backend.global.user.User;

import java.util.Set;

public record UserResponse(Long id, String email, String name, String pictureUrl, Set<Role> roles) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(),
                user.getPictureUrl(), user.getRoles());
    }
}