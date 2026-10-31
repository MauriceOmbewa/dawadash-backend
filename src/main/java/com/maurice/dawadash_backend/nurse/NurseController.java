package com.maurice.dawadash_backend.nurse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nurse")
public class NurseController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Nurse area";
    }
}