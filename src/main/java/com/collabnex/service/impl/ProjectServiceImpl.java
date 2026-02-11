package com.collabnex.service.impl;

import com.collabnex.common.dto.ProjectDto;
import com.collabnex.domain.project.Project;
import com.collabnex.domain.project.ProjectRepository;
import com.collabnex.domain.project.ProjectStatus;
import com.collabnex.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public ProjectDto create(Long clientId, ProjectDto dto) {
        Project project = Project.builder()
                .clientId(clientId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .budget(dto.getBudget())
                .expectedDurationDays(dto.getExpectedDurationDays())
                .currentStatus(ProjectStatus.DRAFT)
                .build();

        return toDto(projectRepository.save(project));
    }

    @Override
    public List<ProjectDto> getMyProjects(Long clientId) {
        return projectRepository.findByClientId(clientId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ProjectDto update(Long clientId, Long projectId, ProjectDto dto) {
        Project project = projectRepository
                .findByIdAndClientId(projectId, clientId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setBudget(dto.getBudget());
        project.setExpectedDurationDays(dto.getExpectedDurationDays());

        return toDto(projectRepository.save(project));
    }

    @Override
    public void delete(Long clientId, Long projectId) {
        Project project = projectRepository
                .findByIdAndClientId(projectId, clientId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.delete(project);
    }

    private ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .budget(project.getBudget())
                .expectedDurationDays(project.getExpectedDurationDays())
                .currentStatus(project.getCurrentStatus().name())
                .build();
    }
}
