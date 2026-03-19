package com.collabnex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration for password hashing to avoid circular dependencies.
 */
@Configuration
public class PasswordConfig {

    /**
     * BCrypt password encoder with strength 12 for hashing user passwords.
     *
     * @return a configured {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
