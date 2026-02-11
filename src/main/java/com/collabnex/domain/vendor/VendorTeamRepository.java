package com.collabnex.domain.vendor;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorTeamRepository extends JpaRepository<VendorTeam, Long> {

    List<VendorTeam> findAllByVendorId(Long vendorId);
}
