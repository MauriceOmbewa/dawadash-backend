package com.maurice.dawadash_backend.admin;

import com.maurice.dawadash_backend.global.auth.UserResponse;
import com.maurice.dawadash_backend.global.user.Role;
import com.maurice.dawadash_backend.global.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /** POST /api/admin/users/5/roles   body: { "role": "DOCTOR" } */
    @PostMapping("/users/{id}/roles")
    public UserResponse addRole(@PathVariable Long id, @RequestBody ChangeRoleRequest request) {
        return UserResponse.from(userService.addRole(id, request.role()));
    }

    /** DELETE /api/admin/users/5/roles/DOCTOR */
    @DeleteMapping("/users/{id}/roles/{role}")
    public UserResponse removeRole(@PathVariable Long id, @PathVariable Role role) {
        return UserResponse.from(userService.removeRole(id, role));
    }
}