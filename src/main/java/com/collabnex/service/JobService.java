package com.collabnex.service;

import com.collabnex.common.dto.*;

import java.util.List;

/**
 * Service interface for job management operations including posting, applying,
 * viewing, and shortlisting.
 */
public interface JobService {

    /**
     * Creates a new job posting for the given client.
     *
     * @param clientId    the posting client's user ID (from JWT)
     * @param title       the job title
     * @param description the job description
     * @return the created JobDto
     */
    JobDto createJob(Long clientId, String title, String description);

    /**
     * Returns all jobs posted by a specific client, ordered by newest first.
     *
     * @param clientId the client's user ID
     * @return list of the client's jobs
     */
    List<JobDto> getJobsByClient(Long clientId);

    /**
     * Returns all jobs for the freelancer feed, sorted by paid-client status (paid first)
     * then by most recently posted.
     *
     * @return sorted list of all jobs
     */
    List<JobDto> getAllJobsForFreelancer();

    /**
     * Returns all jobs for admin view, including client name information.
     *
     * @return list of all jobs with client details
     */
    List<JobDto> getAllJobsForAdmin();

    /**
     * Allows a freelancer to apply for a specific job.
     *
     * @param freelancerId the freelancer's user ID (from JWT)
     * @param jobId        the job to apply to
     * @throws com.collabnex.common.exception.BusinessException if already applied (409)
     * @throws com.collabnex.common.exception.NotFoundException if job not found
     */
    void applyForJob(Long freelancerId, Long jobId);

    /**
     * Returns all applications for a specific job, sorted by paid-member status
     * (paid first) then by application time ascending.
     *
     * @param jobId the job's ID
     * @return list of applicant DTOs
     */
    List<JobApplicationDto> getApplicationsForJob(Long jobId);

    /**
     * Replaces the shortlist for a specific job with the given freelancer IDs.
     * Validates that 3-4 IDs are provided and all have existing applications.
     *
     * @param jobId         the job's ID
     * @param freelancerIds the freelancer IDs to shortlist (3-4 required)
     * @throws com.collabnex.common.exception.BusinessException if validation fails
     */
    void shortlistCandidates(Long jobId, List<Long> freelancerIds);

    /**
     * Returns the shortlisted candidates for a specific job.
     * Used by clients to view who was selected by admin.
     *
     * @param jobId    the job's ID
     * @param clientId the client's user ID (for ownership check)
     * @return list of shortlisted candidate DTOs with contact info
     * @throws com.collabnex.common.exception.BusinessException if the client doesn't own the job
     */
    List<ShortlistedCandidateDto> getShortlistedCandidates(Long jobId, Long clientId);
}
