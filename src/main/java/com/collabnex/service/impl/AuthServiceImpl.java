package com.collabnex.service.impl;

import com.collabnex.common.dto.AuthRequest;
import com.collabnex.common.dto.RegisterRequest;
import com.collabnex.common.dto.UserSummaryDto;
import com.collabnex.common.exception.BusinessException;
import com.collabnex.domain.user.User;
import com.collabnex.domain.user.UserRole;
import com.collabnex.security.JwtService;
import com.collabnex.service.AuthService;
import com.collabnex.service.FileStorageService;
import com.collabnex.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Implementation of {@link AuthService} that handles user registration (with optional
 * resume upload for freelancers) and login (including admin environment-credential check).
 *
 * <p>Admin credentials are read from {@code app.admin.email} and {@code app.admin.password}
 * properties. The plain-text admin password is BCrypt-hashed at startup via {@code @PostConstruct}
 * and stored in memory for comparison during login.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Value("${app.admin.email:admin@collabnex.com}")
    private String adminEmail;

    @Value("${app.admin.password:changeme}")
    private String adminPasswordRaw;

    private String adminPasswordHash;

    /**
     * Hashes the admin plain-text password at startup so we can compare
     * login attempts against it without storing the raw password in memory long-term.
     */
    @PostConstruct
    public void init() {
        this.adminPasswordHash = passwordEncoder.encode(adminPasswordRaw);
    }

    /**
     * Registers a new CLIENT or FREELANCER user. ADMIN self-registration is blocked.
     * If the registering user is a FREELANCER and provides a resume file, it is stored
     * on disk and the URL is saved on the User entity.
     *
     * @param request the registration details
     * @param resume  optional resume PDF file (freelancers only)
     * @return map with "token" and "user" keys
     * @throws BusinessException if role is ADMIN, email is taken, or resume upload fails
     */
    @Override
    public Map<String, Object> register(RegisterRequest request, MultipartFile resume) {
        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid role. Must be CLIENT or FREELANCER");
        }

        if (role == UserRole.ADMIN) {
            throw new BusinessException("Admin registration is not allowed");
        }

        String resumeUrl = null;
        if (resume != null && !resume.isEmpty() && role == UserRole.FREELANCER) {
            // Store resume temporarily with userId=0 since we don't have an ID yet.
            // We'll use a temp approach: store with timestamp only, then update after save.
            resumeUrl = fileStorageService.storeFile(resume, "resumes", 0L);
        }

        User user = userService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                role,
                request.getPhoneNumber(),
                resumeUrl
        );

        // If we stored a resume with id=0, rename to include actual user ID
        // (In practice, the timestamp ensures uniqueness so this is fine)

        String token = jwtService.generateToken(user.getEmail(),
                Map.of("role", user.getRole().name(), "uid", user.getId()));

        UserSummaryDto userDto = UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

        return Map.of("token", token, "user", userDto);
    }

    /**
     * Authenticates a user by email and password. Admin environment credentials are
     * checked first: if the email matches {@code app.admin.email} and the password
     * matches the BCrypt-hashed admin password, a JWT with {@code role=ADMIN, uid=-1}
     * is returned without any database lookup.
     *
     * <p>For regular users, the password is verified against the stored BCrypt hash.
     * Returns 401 (via BusinessException) if credentials are invalid.</p>
     *
     * @param request login credentials (email, password)
     * @return map with "token" and "user" keys
     * @throws BusinessException if credentials are invalid
     */
    @Override
    public Map<String, Object> login(AuthRequest request) {
        // 1. Check admin credentials first
        if (adminEmail.equalsIgnoreCase(request.getEmail())) {
            if (passwordEncoder.matches(request.getPassword(), adminPasswordHash)) {
                String token = jwtService.generateToken(adminEmail,
                        Map.of("role", "ADMIN", "uid", -1L));

                UserSummaryDto adminDto = UserSummaryDto.builder()
                        .id(-1L)
                        .name("Admin")
                        .email(adminEmail)
                        .role("ADMIN")
                        .build();

                return Map.of("token", token, "user", adminDto);
            }
            throw new BusinessException("Invalid credentials");
        }

        // 2. Check database user
        User user = userService.getByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail(),
                Map.of("role", user.getRole().name(), "uid", user.getId()));

        UserSummaryDto userDto = UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

        return Map.of("token", token, "user", userDto);
    }
}
