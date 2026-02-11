package com.collabnex.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByClientId(Long clientId);

    Optional<Project> findByIdAndClientId(Long id, Long clientId);
}
