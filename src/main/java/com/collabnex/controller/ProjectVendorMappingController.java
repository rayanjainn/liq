package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.ProjectVendorMappingDto;
import com.collabnex.service.ProjectVendorMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-vendor-mappings")
@RequiredArgsConstructor
public class ProjectVendorMappingController {

    private final ProjectVendorMappingService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectVendorMappingDto>> assign(
            @AuthenticationPrincipal(expression = "id") Long adminUserId,
            @RequestBody ProjectVendorMappingDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(service.assign(adminUserId, dto))
        );
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<ProjectVendorMappingDto>>> byProject(
            @PathVariable("projectId") Long projectId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(service.getByProject(projectId))
        );
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<List<ProjectVendorMappingDto>>> byVendor(
            @PathVariable("vendorId") Long vendorId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(service.getByVendor(vendorId))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal(expression = "id") Long adminUserId,
            @PathVariable("id") Long id
    ) {
        service.delete(adminUserId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
