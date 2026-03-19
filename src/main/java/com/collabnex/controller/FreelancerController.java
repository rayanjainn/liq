package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.JobDto;
import com.collabnex.config.CustomUserDetails;
import com.collabnex.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for freelancer-specific endpoints.
 * All endpoints under {@code /freelancer} require the FREELANCER role (enforced via SecurityConfig).
 */
@RestController
@RequestMapping("/freelancer")
@RequiredArgsConstructor
public class FreelancerController {

    private final JobService jobService;

    /**
     * GET /freelancer/jobs
     * Access: FREELANCER
     * Description: Retrieves all available jobs, ordered so that jobs from paid clients
     * appear first, then by most recently posted. This gives paid clients higher visibility.
     *
     * @param currentUser the authenticated freelancer (extracted from JWT by Spring Security)
     * @return ApiResponse containing a sorted list of JobDto objects
     */
    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<JobDto>>> viewJobs(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<JobDto> jobs = jobService.getAllJobsForFreelancer();
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    /**
     * POST /freelancer/jobs/{jobId}/apply
     * Access: FREELANCER
     * Description: Apply for a specific job. Each freelancer can only apply once per job.
     * A duplicate application returns 409 Conflict.
     *
     * @param currentUser the authenticated freelancer
     * @param jobId       the ID of the job to apply to
     * @return ApiResponse with success message
     * @throws com.collabnex.common.exception.BusinessException 409 if already applied
     * @throws com.collabnex.common.exception.NotFoundException if job not found
     */
    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApiResponse<String>> applyForJob(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("jobId") Long jobId
    ) {
        jobService.applyForJob(currentUser.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.ok("Application submitted successfully.", null));
    }
}
