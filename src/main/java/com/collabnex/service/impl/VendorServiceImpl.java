package com.collabnex.service.impl;

import com.collabnex.common.dto.*;
import com.collabnex.common.exception.BusinessException;
import com.collabnex.common.exception.NotFoundException;
import com.collabnex.domain.user.User;
import com.collabnex.domain.user.UserRepository;
import com.collabnex.domain.vendor.*;
import com.collabnex.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final VendorCapabilitiesRepository capabilitiesRepository;
    private final UserRepository userRepository;

    @Override
    public VendorProfileDto createVendorProfile(Long userId, VendorProfileRequest req) {

        if (vendorRepository.existsByUserId(userId)) {
            throw new BusinessException("Vendor profile already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Vendor vendor = Vendor.builder()
                .user(user)
                .companyName(req.getCompanyName())
                .vendorType(req.getVendorType())
                .bio(req.getBio())
                .gstNumber(req.getGstNumber())
                .panNumber(req.getPanNumber())
                .addressLine1(req.getAddressLine1())
                .addressLine2(req.getAddressLine2())
                .city(req.getCity())
                .state(req.getState())
                .country(req.getCountry())
                .pincode(req.getPincode())
                .yearStarted(req.getYearStarted())
                .teamSize(req.getTeamSize())
                .websiteUrl(req.getWebsiteUrl())
                .build();

        vendorRepository.save(vendor);

        return toProfileDto(vendor);
    }

    @Override
    public VendorProfileDto getMyVendorProfile(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor profile not found"));
        return toProfileDto(vendor);
    }

    @Override
    public VendorProfileDto updateVendorProfile(Long userId, VendorProfileRequest req) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor profile not found"));

        if (req.getBio() != null) vendor.setBio(req.getBio());
        if (req.getCity() != null) vendor.setCity(req.getCity());
        if (req.getState() != null) vendor.setState(req.getState());
        if (req.getCountry() != null) vendor.setCountry(req.getCountry());
        if (req.getWebsiteUrl() != null) vendor.setWebsiteUrl(req.getWebsiteUrl());
        if (req.getTeamSize() != null) vendor.setTeamSize(req.getTeamSize());

        return toProfileDto(vendor);
    }

    @Override
    public VendorCapabilitiesDto updateCapabilities(Long userId, VendorCapabilitiesDto dto) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor not found"));

        VendorCapabilities capabilities = capabilitiesRepository
                .findByVendorId(vendor.getId())
                .orElse(VendorCapabilities.builder().vendor(vendor).build());

        capabilities.setMinBudget(dto.getMinBudget());
        capabilities.setMaxBudget(dto.getMaxBudget());
        capabilities.setAvgTurnaroundDays(dto.getAvgTurnaroundDays());
        capabilities.setMaxConcurrentProjects(dto.getMaxConcurrentProjects());
        capabilities.setCertifications(dto.getCertifications());

        capabilitiesRepository.save(capabilities);

        return toCapabilitiesDto(capabilities);
    }

    @Override
    public VendorCapabilitiesDto getCapabilities(Long userId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor not found"));

        VendorCapabilities capabilities = capabilitiesRepository
                .findByVendorId(vendor.getId())
                .orElseThrow(() -> new NotFoundException("Capabilities not set"));

        return toCapabilitiesDto(capabilities);
    }

    /* =======================
       MAPPERS
       ======================= */

    private VendorProfileDto toProfileDto(Vendor v) {
        return VendorProfileDto.builder()
                .id(v.getId())
                .companyName(v.getCompanyName())
                .vendorType(v.getVendorType())
                .bio(v.getBio())
                .city(v.getCity())
                .state(v.getState())
                .country(v.getCountry())
                .yearStarted(v.getYearStarted())
                .teamSize(v.getTeamSize())
                .websiteUrl(v.getWebsiteUrl())
                .isPremium(v.getIsPremium())
                .build();
    }

    private VendorCapabilitiesDto toCapabilitiesDto(VendorCapabilities c) {
        return VendorCapabilitiesDto.builder()
                .minBudget(c.getMinBudget())
                .maxBudget(c.getMaxBudget())
                .avgTurnaroundDays(c.getAvgTurnaroundDays())
                .maxConcurrentProjects(c.getMaxConcurrentProjects())
                .certifications(c.getCertifications())
                .build();
    }
}
