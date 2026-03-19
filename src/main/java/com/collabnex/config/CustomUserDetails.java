package com.collabnex.config;

import com.collabnex.domain.user.User;
import com.collabnex.domain.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom {@link UserDetails} implementation that wraps a {@link User} entity.
 * Provides Spring Security with the user's ID, email, password hash, role-based
 * authority, and account status flags.
 *
 * <p>Also serves as the wrapper for the virtual ADMIN user (who is not stored in DB).
 * In that case, the admin-specific constructor is used.</p>
 */
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String name;
    private final String password;
    private final UserRole role;
    private final boolean enabled;

    /**
     * Constructs a CustomUserDetails from a database-persisted User entity.
     *
     * @param user the User entity from the database
     */
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.password = user.getPasswordHash();
        this.role = user.getRole();
        this.enabled = user.getStatus() == null || user.getStatus().name().equals("ACTIVE");
    }

    /**
     * Constructs a CustomUserDetails for the virtual admin (not in the database).
     *
     * @param id       a synthetic admin ID (e.g., -1)
     * @param email    admin email from config
     * @param password BCrypt-hashed admin password
     * @param role     must be {@link UserRole#ADMIN}
     */
    public CustomUserDetails(Long id, String email, String name, String password, UserRole role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.enabled = true;
    }

    /** @return the user's database ID (or -1 for admin) */
    public Long getId() {
        return id;
    }

    /** @return the user's display name */
    public String getName() {
        return name;
    }

    /** @return the user's role enum */
    public UserRole getRole() {
        return role;
    }

    /**
     * Returns a singleton list with the user's role-based authority (ROLE_CLIENT, ROLE_FREELANCER, or ROLE_ADMIN).
     *
     * @return authorities for Spring Security
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
