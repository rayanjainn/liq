package com.collabnex.controller;

import com.collabnex.common.dto.*;
import com.collabnex.common.dto.ApiResponse;
import com.collabnex.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<VendorProfileDto>> createProfile(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody VendorProfileRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(vendorService.createVendorProfile(userId, request))
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<VendorProfileDto>> getProfile(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(vendorService.getMyVendorProfile(userId))
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<VendorProfileDto>> updateProfile(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody VendorProfileRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(vendorService.updateVendorProfile(userId, request))
        );
    }

    @PutMapping("/capabilities")
    public ResponseEntity<ApiResponse<VendorCapabilitiesDto>> updateCapabilities(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody VendorCapabilitiesDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(vendorService.updateCapabilities(userId, dto))
        );
    }

    @GetMapping("/capabilities")
    public ResponseEntity<ApiResponse<VendorCapabilitiesDto>> getCapabilities(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(vendorService.getCapabilities(userId))
        );
    }
}
