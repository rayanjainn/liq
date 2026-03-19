package com.collabnex.common.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for admin shortlisting operation.
 * Contains the list of freelancer IDs to shortlist for a specific job.
 */
@Data
public class ShortlistRequest {

    @NotEmpty(message = "freelancerIds must not be empty")
    @Size(min = 3, max = 4, message = "Must shortlist between 3 and 4 freelancers")
    private List<Long> freelancerIds;
}
