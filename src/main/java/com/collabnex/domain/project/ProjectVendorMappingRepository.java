package com.collabnex.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectVendorMappingRepository
        extends JpaRepository<ProjectVendorMapping, Long> {

    List<ProjectVendorMapping> findByProjectId(Long projectId);

    List<ProjectVendorMapping> findByVendorId(Long vendorId);

    Optional<ProjectVendorMapping> findByIdAndAssignedBy(Long id, Long assignedBy);
}
