package com.collabnex.service;

import com.collabnex.common.dto.AuthRequest;
import com.collabnex.common.dto.RegisterRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Service interface for authentication operations (login and registration).
 */
public interface AuthService {

    /**
     * Registers a new CLIENT or FREELANCER user. Freelancers may optionally upload
     * a resume file during registration.
     *
     * @param request the registration details (name, email, password, role, phoneNumber)
     * @param resume  optional resume file (PDF, for freelancers only)
     * @return a map containing "token" (JWT string) and "user" (UserSummaryDto)
     */
    Map<String, Object> register(RegisterRequest request, MultipartFile resume);

    /**
     * Authenticates a user by email and password. Checks admin environment credentials
     * first, then falls back to database lookup.
     *
     * @param request the login credentials (email, password)
     * @return a map containing "token" (JWT string) and "user" (UserSummaryDto)
     */
    Map<String, Object> login(AuthRequest request);
}
