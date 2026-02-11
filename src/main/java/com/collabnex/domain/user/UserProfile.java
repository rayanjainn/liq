package com.collabnex.domain.user;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(name = "organization_name", length = 255)
    private String organizationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", length = 20)
    private OrganizationType organizationType;

}
