package com.collabnex.common.dto;

import lombok.Data;

@Data
public class VendorMetricsRequest {
    private Integer completedProjects;
    private Integer failedProjects;
    private Integer avgDeliveryDelayDays;
    private Double reworkPercentage;
}
