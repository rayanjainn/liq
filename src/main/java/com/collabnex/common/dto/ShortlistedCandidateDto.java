package com.collabnex.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for shortlisted candidate information visible to the client.
 * Includes contact details and resume URL so the client can reach out.
 */
@Data
@Builder
public class ShortlistedCandidateDto {
    private Long freelancerId;
    private String name;
    private String email;
    private String phoneNumber;
    private String resumeUrl;
}
