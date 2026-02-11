package com.collabnex.domain.vendor;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "vendor_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false, unique = true)
    private Vendor vendor;

    private Integer completedProjects;
    private Integer failedProjects;

    private Integer avgDeliveryDelayDays;


    private Double reworkPercentage;

    private Instant lastUpdated;

    @PrePersist
    @PreUpdate
    void onUpdate() {
        lastUpdated = Instant.now();
    }
}
