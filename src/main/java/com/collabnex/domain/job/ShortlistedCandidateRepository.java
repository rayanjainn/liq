package com.collabnex.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for {@link ShortlistedCandidate} entities.
 */
public interface ShortlistedCandidateRepository extends JpaRepository<ShortlistedCandidate, Long> {

    /**
     * Finds all shortlisted candidates for a specific job.
     *
     * @param jobId the job's ID
     * @return list of shortlisted candidates
     */
    List<ShortlistedCandidate> findByJobId(Long jobId);

    /**
     * Deletes all shortlisted candidates for a specific job.
     * Used when admin replaces the shortlist.
     *
     * @param jobId the job's ID
     */
    void deleteByJobId(Long jobId);
}
