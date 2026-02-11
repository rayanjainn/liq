package com.collabnex.common.dto;

import com.collabnex.domain.vendor.VendorDocumentType;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class VendorDocumentDto {

    private Long id;
    private VendorDocumentType documentType;
    private String filePath;
    private Boolean isVerified;
}
