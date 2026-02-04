package com.example.backend.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AuthService.UsernameTakenException.class)
    public Map<String, String> usernameTaken() {
        return Map.of("message", "Username already exists");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthService.InvalidCredentialsException.class)
    public Map<String, String> invalidCredentials() {
        return Map.of("message", "Invalid credentials");
    }
}
