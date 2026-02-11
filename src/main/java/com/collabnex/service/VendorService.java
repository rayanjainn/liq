package com.collabnex.service;

import com.collabnex.common.dto.*;

public interface VendorService {

    VendorProfileDto createVendorProfile(Long userId, VendorProfileRequest request);

    VendorProfileDto getMyVendorProfile(Long userId);

    VendorProfileDto updateVendorProfile(Long userId, VendorProfileRequest request);

    VendorCapabilitiesDto updateCapabilities(Long userId, VendorCapabilitiesDto dto);

    VendorCapabilitiesDto getCapabilities(Long userId);
}
