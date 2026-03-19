package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.VendorMetricsDto;
import com.collabnex.config.CustomUserDetails;
import com.collabnex.service.VendorMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for vendor metrics/KPIs.
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/api/vendor/metrics")
@RequiredArgsConstructor
public class VendorMetricsController {

    private final VendorMetricsService metricsService;

    /**
     * POST /api/vendor/metrics
     * Access: Authenticated
     * Description: Add or update vendor metrics.
     *
     * @param currentUser the authenticated user
     * @param dto         the metrics data
     * @return the saved VendorMetricsDto
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VendorMetricsDto>> add(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody VendorMetricsDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(metricsService.addOrUpdate(currentUser.getId(), dto))
        );
    }

    /**
     * GET /api/vendor/metrics
     * Access: Authenticated
     * Description: Get the authenticated vendor's metrics.
     *
     * @param currentUser the authenticated user
     * @return the vendor's VendorMetricsDto
     */
    @GetMapping
    public ResponseEntity<ApiResponse<VendorMetricsDto>> get(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(metricsService.getMyMetrics(currentUser.getId()))
        );
    }

    /**
     * PUT /api/vendor/metrics/{id}
     * Access: Authenticated
     * Description: Update vendor metrics by ID.
     *
     * @param currentUser the authenticated user
     * @param id          the metrics record ID
     * @param dto         the updated metrics data
     * @return the updated VendorMetricsDto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorMetricsDto>> update(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("id") Long id,
            @RequestBody VendorMetricsDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(metricsService.update(currentUser.getId(), id, dto))
        );
    }

    /**
     * DELETE /api/vendor/metrics/{id}
     * Access: Authenticated
     * Description: Delete vendor metrics by ID.
     *
     * @param currentUser the authenticated user
     * @param id          the metrics record ID
     * @return empty success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("id") Long id
    ) {
        metricsService.delete(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
