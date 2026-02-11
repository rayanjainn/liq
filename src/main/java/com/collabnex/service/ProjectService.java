package com.collabnex.service;

import com.collabnex.common.dto.ProjectDto;

import java.util.List;

public interface ProjectService {

    ProjectDto create(Long clientId, ProjectDto dto);

    List<ProjectDto> getMyProjects(Long clientId);

    ProjectDto update(Long clientId, Long projectId, ProjectDto dto);

    void delete(Long clientId, Long projectId);
}
