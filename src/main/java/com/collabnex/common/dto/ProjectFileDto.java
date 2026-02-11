package com.collabnex.common.dto;

import com.collabnex.domain.project.ProjectFileType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectFileDto {

    private Long id;
    private Long projectId;
    private String fileName;
    private ProjectFileType fileType;
    private String storagePath;
    private String checksum;
    private Boolean isEncrypted;
}
