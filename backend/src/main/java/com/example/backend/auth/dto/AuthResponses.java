package com.example.backend.auth.dto;

public class AuthResponses {

    public record UserDto(Long id, String username) {

    }

    public record LoginResponse(String token, UserDto user) {

    }
}
