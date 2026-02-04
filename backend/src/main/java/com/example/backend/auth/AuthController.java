package com.example.backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.auth.dto.AuthRequests;
import com.example.backend.auth.dto.AuthResponses;
import com.example.backend.users.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponses.UserDto signup(@RequestBody @Valid AuthRequests.SignupRequest req) {
        return authService.signup(req.username(), req.password());
    }

    @PostMapping("/auth/login")
    public AuthResponses.LoginResponse login(@RequestBody @Valid AuthRequests.LoginRequest req) {
        return authService.login(req.username(), req.password());
    }

    @GetMapping("/me")
    public AuthResponses.UserDto me(@RequestAttribute(value = "authUserId", required = false) Long userId) {
        if (userId == null) {
            throw new UnauthorizedException();
        }
        var u = userRepository.findById(userId).orElseThrow();
        return new AuthResponses.UserDto(u.getId(), u.getUsername());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedException extends RuntimeException {
    }
}
