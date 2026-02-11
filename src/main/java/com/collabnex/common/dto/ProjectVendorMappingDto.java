package com.collabnex.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectVendorMappingDto {

    private Long id;
    private Long projectId;
    private Long vendorId;
    private Long assignedBy;
}
