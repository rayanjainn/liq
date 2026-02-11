package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.ProjectFileDto;
import com.collabnex.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project/files")
@RequiredArgsConstructor
public class ProjectFileController {

    private final ProjectFileService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectFileDto>> add(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody ProjectFileDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(service.add(userId, dto))
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<List<ProjectFileDto>>> list(
            @PathVariable("projectId") Long projectId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(service.getByProject(projectId))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectFileDto>> update(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("id") Long id,
            @RequestBody ProjectFileDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(service.update(userId, id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("id") Long id
    ) {
        service.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
