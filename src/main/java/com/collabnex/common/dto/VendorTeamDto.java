package com.collabnex.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder    
public class VendorTeamDto {
    private Long id;
    private String role;
    private Integer count;
}
