package com.example.backend.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.auth.dto.AuthResponses;
import com.example.backend.users.User;
import com.example.backend.users.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponses.UserDto signup(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException();
        }
        User u = new User();
        u.setUsername(username); // light case-sensitive
        u.setPasswordHash(passwordEncoder.encode(password));
        u = userRepository.save(u);
        return new AuthResponses.UserDto(u.getId(), u.getUsername());
    }

    public AuthResponses.LoginResponse login(String username, String password) {
        User u = userRepository.findByUsername(username).orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtService.createToken(u.getId(), u.getUsername());
        return new AuthResponses.LoginResponse(token, new AuthResponses.UserDto(u.getId(), u.getUsername()));
    }

    public static class UsernameTakenException extends RuntimeException {
    }

    public static class InvalidCredentialsException extends RuntimeException {
    }
}
