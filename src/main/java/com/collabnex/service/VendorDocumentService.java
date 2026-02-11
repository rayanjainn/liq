package com.collabnex.service;

import com.collabnex.common.dto.VendorDocumentDto;

import java.util.List;

public interface VendorDocumentService {

    VendorDocumentDto addDocument(Long userId, VendorDocumentDto dto);

    List<VendorDocumentDto> getMyDocuments(Long userId);

    VendorDocumentDto updateDocument(Long userId, Long documentId, VendorDocumentDto dto);

    void deleteDocument(Long userId, Long documentId);
}
