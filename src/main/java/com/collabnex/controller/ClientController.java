package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.JobDto;
import com.collabnex.common.dto.ShortlistedCandidateDto;
import com.collabnex.config.CustomUserDetails;
import com.collabnex.service.JobService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for client-specific endpoints.
 * All endpoints under {@code /client} require the CLIENT role (enforced via SecurityConfig).
 */
@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final JobService jobService;

    /**
     * POST /client/jobs
     * Access: CLIENT
     * Description: Create a new job posting. The client ID is extracted from the JWT.
     * The isPaidClient flag is automatically copied from the client's isPaidMember status.
     *
     * @param currentUser the authenticated client (from JWT)
     * @param request     JobRequest containing title and description
     * @return ApiResponse containing the created JobDto with 201 status
     */
    @PostMapping("/jobs")
    public ResponseEntity<ApiResponse<JobDto>> createJob(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody JobRequest request
    ) {
        JobDto job = jobService.createJob(currentUser.getId(), request.getTitle(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(job));
    }

    /**
     * GET /client/jobs
     * Access: CLIENT
     * Description: Retrieve all jobs posted by the authenticated client, ordered by newest first.
     *
     * @param currentUser the authenticated client
     * @return ApiResponse containing list of the client's JobDto objects
     */
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobDto>>> getMyJobs(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<JobDto> jobs = jobService.getJobsByClient(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    /**
     * GET /client/jobs/{jobId}/shortlisted
     * Access: CLIENT
     * Description: View the shortlisted candidates for a specific job.
     * Only allowed if the authenticated client owns the job. Returns freelancer
     * contact details (name, email, phone, resume URL).
     *
     * @param currentUser the authenticated client
     * @param jobId       the ID of the job to view shortlisted candidates for
     * @return ApiResponse containing list of ShortlistedCandidateDto
     * @throws com.collabnex.common.exception.BusinessException if the client doesn't own the job
     */
    @GetMapping("/jobs/{jobId}/shortlisted")
    public ResponseEntity<ApiResponse<List<ShortlistedCandidateDto>>> getShortlisted(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("jobId") Long jobId
    ) {
        List<ShortlistedCandidateDto> candidates = jobService.getShortlistedCandidates(jobId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(candidates));
    }

    /**
     * Request body for creating a new job.
     */
    @Data
    public static class JobRequest {
        @NotBlank(message = "Title is required")
        private String title;
        private String description;
    }
}
