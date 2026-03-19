package com.collabnex.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for job application data shown to admins.
 * Includes freelancer info and paid-member status for sorting.
 */
@Data
@Builder
public class JobApplicationDto {
    private Long freelancerId;
    private String name;
    private String email;
    private boolean isPaidMember;
    private LocalDateTime appliedAt;
}
