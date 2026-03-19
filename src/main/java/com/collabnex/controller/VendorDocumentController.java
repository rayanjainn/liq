package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.VendorDocumentDto;
import com.collabnex.config.CustomUserDetails;
import com.collabnex.service.VendorDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vendor document management.
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/api/vendor/documents")
@RequiredArgsConstructor
public class VendorDocumentController {

    private final VendorDocumentService documentService;

    /**
     * POST /api/vendor/documents
     * Access: Authenticated
     * Description: Add a new document to the vendor's document collection.
     *
     * @param currentUser the authenticated user
     * @param dto         the document details
     * @return the created VendorDocumentDto
     */
    @PostMapping
    public ApiResponse<VendorDocumentDto> add(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody VendorDocumentDto dto
    ) {
        return ApiResponse.ok(
                documentService.addDocument(currentUser.getId(), dto)
        );
    }

    /**
     * GET /api/vendor/documents
     * Access: Authenticated
     * Description: List all documents for the authenticated vendor.
     *
     * @param currentUser the authenticated user
     * @return list of VendorDocumentDto objects
     */
    @GetMapping
    public ApiResponse<List<VendorDocumentDto>> list(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ApiResponse.ok(
                documentService.getMyDocuments(currentUser.getId())
        );
    }

    /**
     * PUT /api/vendor/documents/{id}
     * Access: Authenticated
     * Description: Update an existing vendor document.
     *
     * @param currentUser the authenticated user
     * @param id          the document ID to update
     * @param dto         the updated document details
     * @return the updated VendorDocumentDto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDocumentDto>> update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("id") Long id,
            @RequestBody VendorDocumentDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(documentService.updateDocument(currentUser.getId(), id, dto))
        );
    }

    /**
     * DELETE /api/vendor/documents/{id}
     * Access: Authenticated
     * Description: Delete a vendor document by ID.
     *
     * @param currentUser the authenticated user
     * @param id          the document ID to delete
     * @return empty success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("id") Long id
    ) {
        documentService.deleteDocument(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
