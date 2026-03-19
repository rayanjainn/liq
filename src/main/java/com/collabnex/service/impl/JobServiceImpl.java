package com.collabnex.service.impl;

import com.collabnex.common.dto.JobApplicationDto;
import com.collabnex.common.dto.JobDto;
import com.collabnex.common.dto.ShortlistedCandidateDto;
import com.collabnex.common.exception.BusinessException;
import com.collabnex.common.exception.NotFoundException;
import com.collabnex.domain.job.*;
import com.collabnex.domain.user.User;
import com.collabnex.domain.user.UserRepository;
import com.collabnex.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link JobService} containing all business logic for
 * job posting, applying, viewing, and admin shortlisting.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final ShortlistedCandidateRepository shortlistedRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new job posting for the given client. The {@code isPaidClient}
     * flag is copied from the client's {@code isPaidMember} field at creation time
     * so paid clients' jobs naturally sort higher in the freelancer feed.
     *
     * @param clientId    the posting client's user ID
     * @param title       the job title
     * @param description the job description
     * @return the created JobDto
     */
    @Override
    public JobDto createJob(Long clientId, String title, String description) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Job job = Job.builder()
                .title(title)
                .description(description)
                .client(client)
                .isPaidClient(client.isPaidMember())
                .build();

        job = jobRepository.save(job);
        return toDto(job);
    }

    /**
     * Returns all jobs posted by a specific client, ordered by newest first.
     *
     * @param clientId the client's user ID
     * @return list of the client's jobs
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobDto> getJobsByClient(Long clientId) {
        return jobRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Returns all jobs for the freelancer feed, sorted so that jobs from paid clients
     * appear first, then by most recently posted. This ordering gives paid clients
     * higher visibility.
     *
     * @return sorted list of all jobs
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobDto> getAllJobsForFreelancer() {
        return jobRepository.findAllByOrderByIsPaidClientDescCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Returns all jobs for admin view. Includes the client's name for administrative context.
     *
     * @return list of all jobs with client details
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobDto> getAllJobsForAdmin() {
        return jobRepository.findAllByOrderByIsPaidClientDescCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Allows a freelancer to apply for a specific job. Checks for duplicate applications
     * using both a service-level check and a database unique constraint as a safety net.
     *
     * @param freelancerId the freelancer's user ID
     * @param jobId        the job to apply to
     * @throws BusinessException if the freelancer has already applied (409 Conflict)
     * @throws NotFoundException if the job does not exist
     */
    @Override
    public void applyForJob(Long freelancerId, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        User freelancer = userRepository.findById(freelancerId)
                .orElseThrow(() -> new NotFoundException("Freelancer not found"));

        if (applicationRepository.existsByJobIdAndFreelancerId(jobId, freelancerId)) {
            throw new BusinessException("You have already applied for this job");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .freelancer(freelancer)
                .build();

        applicationRepository.save(application);
    }

    /**
     * Returns all applications for a specific job, ordered so that paid-member
     * freelancers appear first, then by earliest application time.
     *
     * @param jobId the job's ID
     * @return list of applicant DTOs with freelancer info
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationDto> getApplicationsForJob(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new NotFoundException("Job not found");
        }

        return applicationRepository.findByJobIdOrderByFreelancerIsPaidMemberDescCreatedAtAsc(jobId)
                .stream()
                .map(app -> JobApplicationDto.builder()
                        .freelancerId(app.getFreelancer().getId())
                        .name(app.getFreelancer().getName())
                        .email(app.getFreelancer().getEmail())
                        .isPaidMember(app.getFreelancer().isPaidMember())
                        .appliedAt(app.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Replaces the existing shortlist for a job with the given freelancer IDs.
     * All previous shortlist entries for this job are deleted before the new ones are saved.
     *
     * <p>Validation:
     * <ul>
     *   <li>Freelancer IDs list must contain 3-4 entries</li>
     *   <li>Every freelancer ID must have an existing application for the job</li>
     * </ul>
     * </p>
     *
     * @param jobId         the job's ID
     * @param freelancerIds the freelancer IDs to shortlist
     * @throws BusinessException if validation fails (wrong count or invalid IDs)
     * @throws NotFoundException if the job does not exist
     */
    @Override
    public void shortlistCandidates(Long jobId, List<Long> freelancerIds) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        if (freelancerIds.size() < 3 || freelancerIds.size() > 4) {
            throw new BusinessException("Must shortlist between 3 and 4 freelancers");
        }

        // Verify all freelancers have applied
        List<JobApplication> applications = applicationRepository.findByJobId(jobId);
        Set<Long> appliedIds = applications.stream()
                .map(app -> app.getFreelancer().getId())
                .collect(Collectors.toSet());

        for (Long fId : freelancerIds) {
            if (!appliedIds.contains(fId)) {
                throw new BusinessException("Freelancer ID " + fId + " has not applied for this job");
            }
        }

        // Replace existing shortlist
        shortlistedRepository.deleteByJobId(jobId);

        for (Long fId : freelancerIds) {
            User freelancer = userRepository.findById(fId)
                    .orElseThrow(() -> new NotFoundException("Freelancer not found: " + fId));

            ShortlistedCandidate candidate = ShortlistedCandidate.builder()
                    .job(job)
                    .freelancer(freelancer)
                    .selectedByAdmin(true)
                    .build();

            shortlistedRepository.save(candidate);
        }
    }

    /**
     * Returns the shortlisted candidates for a specific job. Verifies that the
     * requesting client actually owns the job before returning results.
     *
     * @param jobId    the job's ID
     * @param clientId the client's user ID (for ownership verification)
     * @return list of shortlisted candidate DTOs including contact details
     * @throws BusinessException if the client doesn't own the job
     * @throws NotFoundException if the job doesn't exist
     */
    @Override
    @Transactional(readOnly = true)
    public List<ShortlistedCandidateDto> getShortlistedCandidates(Long jobId, Long clientId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        if (!job.getClient().getId().equals(clientId)) {
            throw new BusinessException("You can only view shortlisted candidates for your own jobs");
        }

        return shortlistedRepository.findByJobId(jobId)
                .stream()
                .map(sc -> ShortlistedCandidateDto.builder()
                        .freelancerId(sc.getFreelancer().getId())
                        .name(sc.getFreelancer().getName())
                        .email(sc.getFreelancer().getEmail())
                        .phoneNumber(sc.getFreelancer().getPhoneNumber())
                        .resumeUrl(sc.getFreelancer().getResumeUrl())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Maps a Job entity to a JobDto for API response.
     *
     * @param job the Job entity
     * @return the corresponding JobDto
     */
    private JobDto toDto(Job job) {
        return JobDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .isPaidClient(job.isPaidClient())
                .clientName(job.getClient().getName())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
