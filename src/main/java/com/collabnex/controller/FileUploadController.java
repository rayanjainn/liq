package com.collabnex.controller;

import com.collabnex.entity.UploadedFile;
import com.collabnex.service.FileUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    // ✅ UPLOAD FILE
    @PostMapping(
            value = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadFile(
            @RequestPart("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is missing");
        }

        try {
            UploadedFile savedFile = fileUploadService.uploadFile(file);

            return ResponseEntity.ok(savedFile); // ✅ RETURN FULL OBJECT

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("File upload failed");
        }
    }

    // ✅ GET ALL FILES
    @GetMapping("/files")
    public ResponseEntity<?> getAllFiles() {
        return ResponseEntity.ok(fileUploadService.getAllFiles());
    }

    // ✅ DELETE FILE (FIXED)
    @DeleteMapping("/files/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable("id") Long id) {
        fileUploadService.deleteFile(id);
        return ResponseEntity.ok("File deleted successfully");
    }
}

