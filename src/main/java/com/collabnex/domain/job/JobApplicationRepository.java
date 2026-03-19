package com.collabnex.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for {@link JobApplication} entities.
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    /**
     * Checks if a freelancer has already applied to a specific job.
     *
     * @param jobId        the job's ID
     * @param freelancerId the freelancer's user ID
     * @return true if an application already exists
     */
    boolean existsByJobIdAndFreelancerId(Long jobId, Long freelancerId);

    /**
     * Finds all applications for a specific job, ordered so paid freelancers come first,
     * then by application time ascending.
     *
     * @param jobId the job's ID
     * @return list of applications for that job
     */
    List<JobApplication> findByJobIdOrderByFreelancerIsPaidMemberDescCreatedAtAsc(Long jobId);

    /**
     * Finds all applications for a given job.
     *
     * @param jobId the job's ID
     * @return list of applications
     */
    List<JobApplication> findByJobId(Long jobId);
}
