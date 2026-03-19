package com.collabnex.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Summary DTO for user information returned after registration/login.
 */
@Data
@Builder
public class UserSummaryDto {
    private Long id;
    private String name;
    private String email;
    private String role;
}
