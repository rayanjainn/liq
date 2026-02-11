package com.collabnex.domain.vendor;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "vendor_documents")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class VendorDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Enumerated(EnumType.STRING)
    private VendorDocumentType documentType;

    private String filePath;

    private Boolean isVerified;

    private Instant uploadedAt;

    @PrePersist
    void onCreate() {
        uploadedAt = Instant.now();
        isVerified = false;
    }
}
