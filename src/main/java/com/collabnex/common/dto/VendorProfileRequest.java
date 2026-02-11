package com.collabnex.common.dto;

import com.collabnex.domain.vendor.VendorType;
import lombok.Data;

@Data
public class VendorProfileRequest {
    private String companyName;
    private VendorType vendorType;
    private String bio;
    private String gstNumber;
    private String panNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private Integer yearStarted;
    private Integer teamSize;
    private String websiteUrl;
}
