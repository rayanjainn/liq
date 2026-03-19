package com.collabnex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration for local file storage, replacing the former S3 integration.
 * Reads the upload directory path from {@code app.storage.upload-dir} and
 * ensures the directory exists at application startup.
 */
@Configuration
public class FileStorageConfig {

    @Value("${app.storage.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Creates and returns the absolute, normalized path to the uploads directory.
     * The directory is created if it does not already exist.
     *
     * @return the resolved {@link Path} to the upload directory
     * @throws IOException if directory creation fails
     */
    @Bean
    public Path uploadPath() throws IOException {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(path);
        return path;
    }
}
