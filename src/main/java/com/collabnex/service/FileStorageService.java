package com.collabnex.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Service interface for local file storage operations.
 * Replaces the former S3-based storage with disk-based storage.
 */
public interface FileStorageService {

    /**
     * Saves a MultipartFile to local disk under {@code uploads/{subDir}/}.
     * The filename format is: {@code {userId}_{timestamp}_{sanitizedOriginalName}}.
     *
     * @param file   the uploaded file
     * @param subDir the subdirectory within uploads (e.g., "resumes", "documents")
     * @param userId the ID of the uploading user
     * @return the relative URL path: {@code /uploads/{subDir}/{filename}}
     * @throws com.collabnex.common.exception.BusinessException if file type is not allowed or other I/O error
     */
    String storeFile(MultipartFile file, String subDir, Long userId);

    /**
     * Deletes a file by its relative URL path (e.g., "/uploads/resumes/42_cv.pdf").
     * Silently ignores if the file does not exist.
     *
     * @param fileUrl the relative URL path of the file
     */
    void deleteFile(String fileUrl);

    /**
     * Resolves a relative URL path to an absolute filesystem Path.
     *
     * @param fileUrl the relative URL path (e.g., "/uploads/resumes/42_cv.pdf")
     * @return the absolute {@link Path} on disk
     */
    Path resolveFilePath(String fileUrl);
}
