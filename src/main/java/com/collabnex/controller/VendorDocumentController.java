package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.VendorDocumentDto;
import com.collabnex.domain.user.User;
import com.collabnex.service.VendorDocumentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/documents")
@RequiredArgsConstructor
public class VendorDocumentController {

    private final VendorDocumentService documentService;

    @PostMapping
    public ApiResponse<VendorDocumentDto> add(
            @AuthenticationPrincipal User user,
            @RequestBody VendorDocumentDto dto
    ) {
        return ApiResponse.ok(
                documentService.addDocument(user.getId(), dto)
        );
    }

    @GetMapping
    public ApiResponse<List<VendorDocumentDto>> list(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(
                documentService.getMyDocuments(user.getId())
        );
    }

@PutMapping("/{id}")
public ResponseEntity<ApiResponse<VendorDocumentDto>> update(
        @AuthenticationPrincipal(expression = "id") Long userId,
        @PathVariable("id") Long id,
        @RequestBody VendorDocumentDto dto
) {
    return ResponseEntity.ok(
            ApiResponse.ok(documentService.updateDocument(userId, id, dto))
    );
}

@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<Void>> delete(
        @AuthenticationPrincipal(expression = "id") Long userId,
        @PathVariable("id") Long id
) {
    documentService.deleteDocument(userId, id);
    return ResponseEntity.ok(ApiResponse.ok(null));
}

}
