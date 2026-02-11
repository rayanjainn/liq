package com.collabnex.domain.vendor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendor_capabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorCapabilities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false, unique = true)
    private Vendor vendor;

    private Double minBudget;
    private Double maxBudget;
    private Integer avgTurnaroundDays;
    private Integer maxConcurrentProjects;

    @Column(columnDefinition = "TEXT")
    private String certifications;
}
