package com.collabnex.service.impl;

import com.collabnex.common.dto.VendorMetricsDto;
import com.collabnex.common.exception.NotFoundException;
import com.collabnex.domain.vendor.*;

import com.collabnex.service.VendorMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorMetricsServiceImpl implements VendorMetricsService {

    private final VendorRepository vendorRepository;
    private final VendorMetricsRepository metricsRepository;

    @Override
    public VendorMetricsDto addOrUpdate(Long userId, VendorMetricsDto dto) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor not found"));

        VendorMetrics metrics = metricsRepository.findByVendorId(vendor.getId())
                .orElse(new VendorMetrics());

        metrics.setVendor(vendor);
        map(dto, metrics);

        metricsRepository.save(metrics);
        return toDto(metrics);
    }

    @Override
    public VendorMetricsDto update(Long userId, Long metricsId, VendorMetricsDto dto) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor not found"));

        VendorMetrics metrics = metricsRepository.findById(metricsId)
                .orElseThrow(() -> new NotFoundException("Metrics not found"));

        if (!metrics.getVendor().getId().equals(vendor.getId())) {
            throw new NotFoundException("Unauthorized metrics access");
        }

        map(dto, metrics);
        metricsRepository.save(metrics);

        return toDto(metrics);
    }

    @Override
    public void delete(Long userId, Long metricsId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor not found"));

        VendorMetrics metrics = metricsRepository.findById(metricsId)
                .orElseThrow(() -> new NotFoundException("Metrics not found"));

        if (!metrics.getVendor().getId().equals(vendor.getId())) {
            throw new NotFoundException("Unauthorized metrics access");
        }

        metricsRepository.delete(metrics);
    }

    @Override
    public VendorMetricsDto getMyMetrics(Long userId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor not found"));

        VendorMetrics metrics = metricsRepository.findByVendorId(vendor.getId())
                .orElseThrow(() -> new NotFoundException("Metrics not found"));

        return toDto(metrics);
    }

    private void map(VendorMetricsDto dto, VendorMetrics m) {
        m.setCompletedProjects(dto.getCompletedProjects());
        m.setFailedProjects(dto.getFailedProjects());
        m.setAvgDeliveryDelayDays(dto.getAvgDeliveryDelayDays());
        m.setReworkPercentage(dto.getReworkPercentage());
    }

    private VendorMetricsDto toDto(VendorMetrics m) {
        VendorMetricsDto dto = new VendorMetricsDto();
        dto.setId(m.getId());
        dto.setCompletedProjects(m.getCompletedProjects());
        dto.setFailedProjects(m.getFailedProjects());
        dto.setAvgDeliveryDelayDays(m.getAvgDeliveryDelayDays());
        dto.setReworkPercentage(m.getReworkPercentage());
        return dto;
    }
}
