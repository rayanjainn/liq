package com.collabnex.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * JPA entity representing a registered user in the CollabNex platform.
 * Users can have roles CLIENT, FREELANCER, or ADMIN (ADMIN is env-only, never persisted).
 * Implements Spring Security's {@link UserDetails} to integrate directly with the security framework.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserStatus status;

    @Column(name = "is_paid_member")
    private boolean isPaidMember;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Lifecycle callback invoked before initial persistence.
     * Sets creation/update timestamps and default role/status if not already set.
     */
    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.role == null) {
            this.role = UserRole.CLIENT;
        }
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
    }

    /**
     * Lifecycle callback invoked before every update.
     * Refreshes the {@code updatedAt} timestamp.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    /* =======================
       Spring Security
       ======================= */

    /**
     * Returns the granted authorities for this user. Maps the user's role to a
     * Spring Security authority with the "ROLE_" prefix (e.g., ROLE_CLIENT).
     *
     * @return a singleton list containing the user's role-based authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
