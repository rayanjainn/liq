package com.collabnex.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity tracking uploaded files. Stores metadata including the file name,
 * content type, and the local disk URL path where the file is stored.
 */
@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@NoArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Original filename as provided by the uploader */
    private String fileName;

    /** MIME content type (e.g., application/pdf, image/jpeg) */
    private String fileType;

    /** Relative URL path to the stored file (e.g., /uploads/files/1_123_doc.pdf) */
    private String fileUrl;
}
