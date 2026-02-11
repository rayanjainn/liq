package com.collabnex.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {

    private Long id;
    private String title;
    private String description;
    private Integer budget;
    private Integer expectedDurationDays;
    private String currentStatus;
}
