package com.collabnex.service.impl;

import com.collabnex.common.dto.ProjectFileDto;
import com.collabnex.domain.project.ProjectFile;
import com.collabnex.domain.project.ProjectFileRepository;
import com.collabnex.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectFileRepository repository;

    @Override
    public ProjectFileDto add(Long userId, ProjectFileDto dto) {

        ProjectFile file = ProjectFile.builder()
                .projectId(dto.getProjectId())
                .uploadedBy(userId)
                .fileName(dto.getFileName())
                .fileType(dto.getFileType())
                .storagePath(dto.getStoragePath())
                .checksum(dto.getChecksum())
                .isEncrypted(dto.getIsEncrypted())
                .build();

        return toDto(repository.save(file));
    }

    @Override
    public List<ProjectFileDto> getByProject(Long projectId) {
        return repository.findByProjectId(projectId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ProjectFileDto update(Long userId, Long id, ProjectFileDto dto) {

        ProjectFile file = repository.findByIdAndUploadedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        file.setFileName(dto.getFileName());
        file.setFileType(dto.getFileType());
        file.setStoragePath(dto.getStoragePath());
        file.setChecksum(dto.getChecksum());
        file.setIsEncrypted(dto.getIsEncrypted());

        return toDto(repository.save(file));
    }

    @Override
    public void delete(Long userId, Long id) {

        ProjectFile file = repository.findByIdAndUploadedBy(id, userId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        repository.delete(file);
    }

    private ProjectFileDto toDto(ProjectFile f) {
        return ProjectFileDto.builder()
                .id(f.getId())
                .projectId(f.getProjectId())
                .fileName(f.getFileName())
                .fileType(f.getFileType())
                .storagePath(f.getStoragePath())
                .checksum(f.getChecksum())
                .isEncrypted(f.getIsEncrypted())
                .build();
    }
}
