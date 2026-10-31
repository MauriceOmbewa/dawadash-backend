package com.maurice.dawadash_backend.hospital;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Hospital area";
    }
}