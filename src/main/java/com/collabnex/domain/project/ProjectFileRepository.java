package com.collabnex.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectFileRepository
        extends JpaRepository<ProjectFile, Long> {

    List<ProjectFile> findByProjectId(Long projectId);

    Optional<ProjectFile> findByIdAndUploadedBy(Long id, Long uploadedBy);
}
