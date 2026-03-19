package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.config.CustomUserDetails;
import com.collabnex.entity.UploadedFile;
import com.collabnex.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for general file upload operations.
 * Uses local disk storage instead of S3.
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * POST /api/upload/file
     * Access: Authenticated users
     * Description: Upload a file to local storage. The file is saved to disk and
     * metadata is persisted in the database. Supported types: PDF, JPEG, PNG, WebP.
     *
     * @param file        the file to upload (multipart)
     * @param currentUser the authenticated user (from JWT)
     * @return the saved UploadedFile entity with metadata
     */
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadedFile>> uploadFile(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File is missing"));
        }

        UploadedFile savedFile = fileUploadService.uploadFile(file, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(savedFile));
    }

    /**
     * GET /api/upload/files
     * Access: Authenticated users
     * Description: Retrieve metadata for all uploaded files.
     *
     * @return list of all UploadedFile entities
     */
    @GetMapping("/files")
    public ResponseEntity<ApiResponse<List<UploadedFile>>> getAllFiles() {
        return ResponseEntity.ok(ApiResponse.ok(fileUploadService.getAllFiles()));
    }

    /**
     * DELETE /api/upload/files/{id}
     * Access: Authenticated users
     * Description: Delete a file by its database ID. Removes both the physical file
     * from disk and the database record.
     *
     * @param id the file's database ID
     * @return success message
     */
    @DeleteMapping("/files/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFile(@PathVariable("id") Long id) {
        fileUploadService.deleteFile(id);
        return ResponseEntity.ok(ApiResponse.ok("File deleted successfully", null));
    }
}
