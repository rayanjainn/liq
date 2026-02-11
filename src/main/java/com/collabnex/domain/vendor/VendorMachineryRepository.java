package com.collabnex.domain.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorMachineryRepository
        extends JpaRepository<VendorMachinery, Long> {

    List<VendorMachinery> findAllByVendorId(Long vendorId);
}
