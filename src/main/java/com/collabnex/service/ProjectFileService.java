package com.collabnex.service;

import com.collabnex.common.dto.ProjectFileDto;

import java.util.List;

public interface ProjectFileService {

    ProjectFileDto add(Long userId, ProjectFileDto dto);

    List<ProjectFileDto> getByProject(Long projectId);

    ProjectFileDto update(Long userId, Long id, ProjectFileDto dto);

    void delete(Long userId, Long id);
}
