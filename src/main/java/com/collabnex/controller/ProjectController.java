package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.ProjectDto;
import com.collabnex.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDto>> create(
            @AuthenticationPrincipal(expression = "id") Long clientId,
            @RequestBody ProjectDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(projectService.create(clientId, dto))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDto>>> list(
            @AuthenticationPrincipal(expression = "id") Long clientId
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(projectService.getMyProjects(clientId))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDto>> update(
            @AuthenticationPrincipal(expression = "id") Long clientId,
            @PathVariable("id") Long id,
            @RequestBody ProjectDto dto
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(projectService.update(clientId, id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal(expression = "id") Long clientId,
            @PathVariable("id") Long id
    ) {
        projectService.delete(clientId, id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
