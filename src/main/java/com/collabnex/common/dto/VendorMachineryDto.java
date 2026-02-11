package com.collabnex.common.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorMachineryDto {

    private Long id;
    private String machineName;
    private String machineType;
    private String manufacturer;
    private Integer quantity;
    private String capabilities;
    private Boolean isOperational;
}
