package com.collabnex.domain.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorMetricsRepository extends JpaRepository<VendorMetrics, Long> {

    Optional<VendorMetrics> findByVendorId(Long vendorId);
}
