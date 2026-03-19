package com.collabnex.service.impl;

import com.collabnex.common.exception.BusinessException;
import com.collabnex.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

/**
 * Implementation of {@link FileStorageService} that stores files on the local filesystem.
 * Files are saved under the configured upload directory, organized by subdirectory.
 *
 * <p>Allowed content types: {@code application/pdf}, {@code image/jpeg}, {@code image/png}, {@code image/webp}.</p>
 * <p>File size limits are enforced by Spring's multipart configuration, not in this service.</p>
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf", "image/jpeg", "image/png", "image/webp"
    );

    private final Path uploadPath;

    /**
     * Constructs the service with the upload path bean from {@link com.collabnex.config.FileStorageConfig}.
     *
     * @param uploadPath the absolute path to the upload directory
     */
    public FileStorageServiceImpl(Path uploadPath) {
        this.uploadPath = uploadPath;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates the file's content type against the allow-list before saving.
     * The original filename is sanitized to remove path-traversal characters.
     * The generated filename includes the user ID and a timestamp for uniqueness.</p>
     */
    @Override
    public String storeFile(MultipartFile file, String subDir, Long userId) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("File type not allowed. Allowed: PDF, JPEG, PNG, WebP");
        }

        try {
            Path targetDir = uploadPath.resolve(subDir);
            Files.createDirectories(targetDir);

            String originalName = file.getOriginalFilename();
            if (originalName == null) originalName = "file";
            // Sanitize: remove any path separator characters
            originalName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");

            String filename = userId + "_" + System.currentTimeMillis() + "_" + originalName;
            Path targetPath = targetDir.resolve(filename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subDir + "/" + filename;
        } catch (IOException e) {
            throw new BusinessException("Failed to store file: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Resolves the relative URL to an absolute path and deletes the file if it exists.
     * No exception is thrown if the file is already missing.</p>
     */
    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            Path filePath = resolveFilePath(fileUrl);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Silently ignore — file may have already been removed
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Strips the leading {@code /uploads/} prefix and resolves against the upload root.</p>
     */
    @Override
    public Path resolveFilePath(String fileUrl) {
        // Strip leading /uploads/ to get the relative path within the uploads dir
        String relativePath = fileUrl.startsWith("/uploads/") ? fileUrl.substring(9) : fileUrl;
        return uploadPath.resolve(relativePath).normalize();
    }
}
