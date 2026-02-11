package com.collabnex.common.dto;

import com.collabnex.domain.vendor.VendorType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorProfileDto {
    private Long id;
    private String companyName;
    private VendorType vendorType;
    private String bio;
    private String city;
    private String state;
    private String country;
    private Integer yearStarted;
    private Integer teamSize;
    private String websiteUrl;
    private Boolean isPremium;
}
