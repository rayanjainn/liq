package com.collabnex.service.impl;

import com.collabnex.common.exception.BusinessException;
import com.collabnex.common.exception.NotFoundException;
import com.collabnex.domain.user.*;
import com.collabnex.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserService} that handles user registration, lookup,
 * and Spring Security's {@code loadUserByUsername} contract.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system. Validates that the email is not already taken,
     * hashes the password with BCrypt (strength 12), and persists both the User entity
     * and an associated UserProfile with the display name.
     *
     * @param name        the user's display name
     * @param email       must be unique (case-insensitive check)
     * @param rawPassword plain-text password — hashed before storage
     * @param role        CLIENT or FREELANCER
     * @param phoneNumber optional phone number
     * @param resumeUrl   optional resume file URL (freelancers)
     * @return the saved User entity with generated ID
     * @throws BusinessException if the email is already registered
     */
    @Override
    public User register(String name, String email, String rawPassword, UserRole role, String phoneNumber, String resumeUrl) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Email already registered");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .status(UserStatus.ACTIVE)
                .phoneNumber(phoneNumber)
                .resumeUrl(resumeUrl)
                .build();

        user = userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(name)
                .phone(phoneNumber)
                .build();

        profileRepository.save(profile);

        return user;
    }

    /**
     * Retrieves a user by their database ID.
     *
     * @param id the user's ID
     * @return the User entity
     * @throws NotFoundException if no user with that ID exists
     */
    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    /**
     * Retrieves a user by email (case-insensitive).
     *
     * @param email the email to search for
     * @return the User entity
     * @throws NotFoundException if no user with that email exists
     */
    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    /**
     * Loads user details by username (email) for Spring Security authentication.
     * This method is called during JWT filter authentication to establish the security context.
     *
     * @param username the user's email address
     * @return the UserDetails (the User entity itself implements UserDetails)
     * @throws UsernameNotFoundException if no user with that email exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
