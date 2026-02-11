package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.VendorMachineryDto;
import com.collabnex.service.VendorMachineryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/machineries")
@RequiredArgsConstructor
public class VendorMachineryController {

    private final VendorMachineryService machineryService;

    @PostMapping
    public ResponseEntity<ApiResponse<VendorMachineryDto>> add(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody VendorMachineryDto dto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                machineryService.addMachinery(userId, dto)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorMachineryDto>>> list(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                machineryService.getMyMachineries(userId)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorMachineryDto>> update(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("id") Long id,
            @RequestBody VendorMachineryDto dto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                machineryService.updateMachinery(userId, id, dto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal(expression = "id") Long userId,
           @PathVariable("id") Long id
    ) {
        machineryService.deleteMachinery(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
