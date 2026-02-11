package com.collabnex.domain.project;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "project_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private ProjectFileType fileType;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(length = 64)
    private String checksum;

    @Column(name = "is_encrypted")
    private Boolean isEncrypted;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        if (isEncrypted == null) {
            isEncrypted = true;
        }
    }
}