package com.collabnex.domain.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorCapabilitiesRepository extends JpaRepository<VendorCapabilities, Long> {

    Optional<VendorCapabilities> findByVendorId(Long vendorId);
}
