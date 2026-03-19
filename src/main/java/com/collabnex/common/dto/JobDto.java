package com.collabnex.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data transfer object representing a job posting.
 * Used in freelancer feed and client job list responses.
 */
@Data
@Builder
public class JobDto {
    private Long id;
    private String title;
    private String description;
    private boolean isPaidClient;
    private String clientName;
    private LocalDateTime createdAt;
}
