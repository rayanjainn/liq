package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.VendorMetricsDto;
import com.collabnex.domain.user.User;
import com.collabnex.service.VendorMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor/metrics")
@RequiredArgsConstructor
public class VendorMetricsController {

    private final VendorMetricsService metricsService;

    @PostMapping
    public ResponseEntity<ApiResponse<VendorMetricsDto>> add(
            @AuthenticationPrincipal User user,
            @RequestBody VendorMetricsDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        metricsService.addOrUpdate(user.getId(), dto)
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<VendorMetricsDto>> get(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        metricsService.getMyMetrics(user.getId())
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorMetricsDto>> update(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long id,
            @RequestBody VendorMetricsDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        metricsService.update(user.getId(), id, dto)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long id
    ) {
        metricsService.delete(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
