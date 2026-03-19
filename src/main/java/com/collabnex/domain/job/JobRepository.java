package com.collabnex.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for {@link Job} entities.
 */
public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Finds all jobs posted by a specific client.
     *
     * @param clientId the ID of the client user
     * @return list of jobs belonging to that client
     */
    List<Job> findByClientIdOrderByCreatedAtDesc(Long clientId);

    /**
     * Retrieves all jobs ordered by paid-client status (paid first) then by newest.
     *
     * @return all jobs sorted for the freelancer feed
     */
    List<Job> findAllByOrderByIsPaidClientDescCreatedAtDesc();
}
