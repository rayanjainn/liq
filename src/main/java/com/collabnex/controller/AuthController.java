package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.AuthRequest;
import com.collabnex.common.dto.RegisterRequest;
import com.collabnex.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST controller for authentication endpoints (register and login).
 * All endpoints under {@code /auth} are publicly accessible.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/register
     * Access: Public
     * Description: Register a new CLIENT or FREELANCER. Freelancers may upload a resume file.
     * The request is multipart/form-data so both JSON fields and the resume file can be sent together.
     *
     * @param name        the user's display name (required)
     * @param email       the user's email address (required, must be valid)
     * @param password    the user's password (required)
     * @param role        the user's role: CLIENT or FREELANCER (required)
     * @param phoneNumber optional phone number
     * @param resume      optional multipart file (PDF only, freelancers only)
     * @return ApiResponse containing JWT token and user summary with 201 status
     * @throws com.collabnex.common.exception.BusinessException 409 if email already exists
     * @throws com.collabnex.common.exception.BusinessException 400 if role is ADMIN
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestPart(value = "resume", required = false) MultipartFile resume
    ) {
        RegisterRequest request = new RegisterRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setRole(role);
        request.setPhoneNumber(phoneNumber);

        Map<String, Object> result = authService.register(request, resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    /**
     * POST /auth/login
     * Access: Public
     * Description: Authenticate a user (CLIENT, FREELANCER, or ADMIN) by email and password.
     * Admin credentials are checked against environment variables first, then the database.
     *
     * @param request AuthRequest containing email and password
     * @return ApiResponse containing JWT token and user summary
     * @throws com.collabnex.common.exception.BusinessException if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody AuthRequest request
    ) {
        Map<String, Object> result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
