package com.collabnex.service.impl;

import com.collabnex.common.dto.ProjectVendorMappingDto;
import com.collabnex.domain.project.ProjectVendorMapping;
import com.collabnex.domain.project.ProjectVendorMappingRepository;
import com.collabnex.service.ProjectVendorMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectVendorMappingServiceImpl
        implements ProjectVendorMappingService {

    private final ProjectVendorMappingRepository repository;

    @Override
    public ProjectVendorMappingDto assign(
            Long adminUserId,
            ProjectVendorMappingDto dto
    ) {
        ProjectVendorMapping mapping = ProjectVendorMapping.builder()
                .projectId(dto.getProjectId())
                .vendorId(dto.getVendorId())
                .assignedBy(adminUserId)
                .build();

        return toDto(repository.save(mapping));
    }

    @Override
    public List<ProjectVendorMappingDto> getByProject(Long projectId) {
        return repository.findByProjectId(projectId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProjectVendorMappingDto> getByVendor(Long vendorId) {
        return repository.findByVendorId(vendorId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void delete(Long adminUserId, Long mappingId) {
        ProjectVendorMapping mapping = repository
                .findByIdAndAssignedBy(mappingId, adminUserId)
                .orElseThrow(() -> new RuntimeException("Mapping not found"));

        repository.delete(mapping);
    }

    private ProjectVendorMappingDto toDto(ProjectVendorMapping m) {
        return ProjectVendorMappingDto.builder()
                .id(m.getId())
                .projectId(m.getProjectId())
                .vendorId(m.getVendorId())
                .assignedBy(m.getAssignedBy())
                .build();
    }
}
