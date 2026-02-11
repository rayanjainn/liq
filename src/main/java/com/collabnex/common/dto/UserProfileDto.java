package com.collabnex.common.dto;

import com.collabnex.domain.user.OrganizationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {
    private Long id;
    private String fullName;
    private String phone;
    private String organizationName;
    private OrganizationType organizationType;
}
