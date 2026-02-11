package com.collabnex.service;

import com.collabnex.common.dto.ProjectVendorMappingDto;

import java.util.List;

public interface ProjectVendorMappingService {

    ProjectVendorMappingDto assign(
            Long adminUserId,
            ProjectVendorMappingDto dto
    );

    List<ProjectVendorMappingDto> getByProject(Long projectId);

    List<ProjectVendorMappingDto> getByVendor(Long vendorId);

    void delete(Long adminUserId, Long mappingId);
}
