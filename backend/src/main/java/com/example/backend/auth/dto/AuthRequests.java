package com.example.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequests {

    public record SignupRequest(
            @NotBlank
            @Size(min = 3, max = 30) String username,
            @NotBlank
            @Size(min = 8, max = 100) String password
            ) {

    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
            ) {

    }
}
