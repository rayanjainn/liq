package com.collabnex.service;

import com.collabnex.entity.UploadedFile;
import com.collabnex.repository.UploadedFileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service for managing uploaded files. Uses {@link FileStorageService} for local disk
 * operations and {@link UploadedFileRepository} for metadata persistence.
 * Replaces the former S3-based implementation.
 */
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileStorageService fileStorageService;
    private final UploadedFileRepository repository;

    /**
     * Uploads a file to local disk and saves metadata in the database.
     * Files are stored under {@code uploads/files/} directory.
     *
     * @param file   the multipart file to upload
     * @param userId the ID of the uploading user
     * @return the persisted UploadedFile entity with generated ID and fileUrl
     */
    public UploadedFile uploadFile(MultipartFile file, Long userId) {
        String fileUrl = fileStorageService.storeFile(file, "files", userId);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setFileType(file.getContentType());
        uploadedFile.setFileUrl(fileUrl);

        return repository.save(uploadedFile);
    }

    /**
     * Retrieves metadata for all uploaded files.
     *
     * @return list of all UploadedFile entities
     */
    public List<UploadedFile> getAllFiles() {
        return repository.findAll();
    }

    /**
     * Deletes a file by its database ID. Removes both the physical file from disk
     * and the database record. The database record is deleted first for consistency.
     *
     * @param id the file's database ID
     * @throws RuntimeException if the file ID is not found
     */
    @Transactional
    public void deleteFile(Long id) {
        UploadedFile file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String fileUrl = file.getFileUrl();

        // Delete DB record first
        repository.delete(file);

        // Delete physical file
        if (fileUrl != null && !fileUrl.isEmpty()) {
            fileStorageService.deleteFile(fileUrl);
        }
    }
}
