package com.collabnex.domain.vendor;

import com.collabnex.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_type", nullable = false, length = 20)
    private VendorType vendorType;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "gst_number", unique = true)
    private String gstNumber;

    @Column(name = "pan_number", unique = true)
    private String panNumber;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pincode;

    private Integer yearStarted;
    private Integer teamSize;

    private String websiteUrl;

    private Boolean isPremium = false;

    @Enumerated(EnumType.STRING)
    private VendorSubscriptionStatus subscriptionStatus = VendorSubscriptionStatus.EXPIRED;

    private Double rating = 0.0;

    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
