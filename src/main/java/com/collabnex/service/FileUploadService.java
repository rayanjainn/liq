package com.collabnex.service;

import com.collabnex.entity.UploadedFile;
import com.collabnex.repository.UploadedFileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileUploadService {

    private final S3Service s3Service;
    private final UploadedFileRepository repository;

    public FileUploadService(S3Service s3Service,
                             UploadedFileRepository repository) {
        this.s3Service = s3Service;
        this.repository = repository;
    }

    // ===== UPLOAD =====
    public UploadedFile uploadFile(MultipartFile file) {

        String s3Key = "LayerIQ/xyz/"
                + System.currentTimeMillis()
                + "_" + file.getOriginalFilename();

        String fileUrl = s3Service.uploadFile(file, s3Key);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setFileType(file.getContentType());
        uploadedFile.setFileUrl(fileUrl);
        uploadedFile.setS3Key(s3Key);

        return repository.save(uploadedFile);
    }

    // ===== GET ALL =====
    public List<UploadedFile> getAllFiles() {
        return repository.findAll();
    }

    // ===== DELETE (🔥 FIXED PROPERLY) =====
    @Transactional
    public void deleteFile(Long id) {

        UploadedFile file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String s3Key = file.getS3Key();

        // ✅ DELETE DB FIRST
        repository.delete(file);

        // ✅ DELETE S3 AFTER DB DELETE
        if (s3Key != null && !s3Key.isEmpty()) {
            s3Service.deleteFile(s3Key);
        }

        System.out.println("✅ FILE DELETED ID = " + id);
    }
}

