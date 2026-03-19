package com.collabnex.service;

import com.collabnex.domain.user.User;
import com.collabnex.domain.user.UserRole;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Service interface for user management operations.
 * Extends {@link UserDetailsService} for Spring Security integration.
 */
public interface UserService extends UserDetailsService {

    /**
     * Registers a new user with the given details.
     *
     * @param name        the user's display name
     * @param email       the user's email (must be unique)
     * @param rawPassword the plain-text password (will be hashed)
     * @param role        the user's role (CLIENT or FREELANCER)
     * @param phoneNumber optional phone number
     * @param resumeUrl   optional resume URL (for freelancers)
     * @return the persisted User entity
     */
    User register(String name, String email, String rawPassword, UserRole role, String phoneNumber, String resumeUrl);

    /**
     * Retrieves a user by their database ID.
     *
     * @param id the user's ID
     * @return the User entity
     * @throws com.collabnex.common.exception.NotFoundException if no user with that ID exists
     */
    User getById(Long id);

    /**
     * Retrieves a user by their email address (case-insensitive).
     *
     * @param email the user's email
     * @return the User entity
     * @throws com.collabnex.common.exception.NotFoundException if no user with that email exists
     */
    User getByEmail(String email);
}
