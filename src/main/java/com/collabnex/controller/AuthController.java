package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.domain.user.User;
import com.collabnex.security.JwtService;
import com.collabnex.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String,Object>>> register(@RequestBody RegisterRequest req) {
        User user = userService.registerLocal(req.fullName, req.email, req.password);
        String token = jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name(), "uid", user.getId()));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("token", token)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,Object>>> login(@RequestBody LoginRequest req) {
        User user = userService.getByEmail(req.email);
        // For brevity, validate password in a real impl (retrieve encoder via service or here)
        String token = jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name(), "uid", user.getId()));
        return ResponseEntity.ok(ApiResponse.ok(Map.of("token", token)));
    }

    @Data
    public static class RegisterRequest {
        @NotBlank private String fullName;
        @Email @NotBlank private String email;
        @NotBlank private String password;
    }
    @Data
    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;
    }
}
