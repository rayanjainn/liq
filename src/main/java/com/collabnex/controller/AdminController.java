package com.collabnex.controller;

import com.collabnex.common.dto.*;
import com.collabnex.config.CustomUserDetails;
import com.collabnex.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for admin-specific endpoints.
 * All endpoints under {@code /admin} require the ADMIN role (enforced via SecurityConfig).
 * The admin user is authenticated via environment credentials, not from the database.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JobService jobService;

    /**
     * GET /admin/jobs
     * Access: ADMIN
     * Description: View all jobs posted by all clients. Includes client name for context.
     * Sorted by paid clients first, then newest first.
     *
     * @param currentUser the authenticated admin (virtual, uid=-1)
     * @return ApiResponse containing list of all JobDto objects
     */
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobDto>>> getAllJobs(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<JobDto> jobs = jobService.getAllJobsForAdmin();
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    /**
     * GET /admin/jobs/{jobId}/applications
     * Access: ADMIN
     * Description: View all applicants for a specific job. Sorted by paid-member status
     * (paid first), then by application time ascending. This helps admin identify
     * premium candidates quickly.
     *
     * @param currentUser the authenticated admin
     * @param jobId       the ID of the job to view applications for
     * @return ApiResponse containing list of JobApplicationDto objects
     * @throws com.collabnex.common.exception.NotFoundException if job not found
     */
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<List<JobApplicationDto>>> getApplications(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("jobId") Long jobId
    ) {
        List<JobApplicationDto> applications = jobService.getApplicationsForJob(jobId);
        return ResponseEntity.ok(ApiResponse.ok(applications));
    }

    /**
     * POST /admin/jobs/{jobId}/shortlist
     * Access: ADMIN
     * Description: Save 3-4 shortlisted freelancer IDs for a job. Replaces any existing
     * shortlist for this job. All provided freelancer IDs must have an existing application.
     *
     * @param currentUser the authenticated admin
     * @param jobId       the ID of the job to set shortlist for
     * @param request     ShortlistRequest containing freelancerIds (3-4 required)
     * @return ApiResponse with success message
     * @throws com.collabnex.common.exception.BusinessException 400 if validation fails
     */
    @PostMapping("/jobs/{jobId}/shortlist")
    public ResponseEntity<ApiResponse<String>> shortlist(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("jobId") Long jobId,
            @Valid @RequestBody ShortlistRequest request
    ) {
        jobService.shortlistCandidates(jobId, request.getFreelancerIds());
        return ResponseEntity.ok(ApiResponse.ok("Shortlist saved successfully.", null));
    }
}
