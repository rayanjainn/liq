package com.collabnex.service;

import com.collabnex.common.dto.VendorMetricsDto;

public interface VendorMetricsService {

    VendorMetricsDto addOrUpdate(Long userId, VendorMetricsDto dto);

    VendorMetricsDto update(Long userId, Long metricsId, VendorMetricsDto dto);

    void delete(Long userId, Long metricsId);

    VendorMetricsDto getMyMetrics(Long userId);
}
