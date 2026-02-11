package com.collabnex.common.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorCapabilitiesDto {

    private Double minBudget;
    private Double maxBudget;
    private Integer avgTurnaroundDays;
    private Integer maxConcurrentProjects;
    private String certifications;
}
