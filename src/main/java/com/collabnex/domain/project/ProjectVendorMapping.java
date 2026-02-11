package com.collabnex.domain.project;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "project_vendor_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectVendorMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "assigned_at", updatable = false)
    private Instant assignedAt;

    @PrePersist
    void onCreate() {
        assignedAt = Instant.now();
    }
}
