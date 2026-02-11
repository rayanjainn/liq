package com.collabnex.domain.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorDocumentRepository
        extends JpaRepository<VendorDocument, Long> {

    // All documents of a vendor
    List<VendorDocument> findByVendorId(Long vendorId);

    // Used for update & delete (ownership check)
    Optional<VendorDocument> findByIdAndVendorId(Long id, Long vendorId);
}
