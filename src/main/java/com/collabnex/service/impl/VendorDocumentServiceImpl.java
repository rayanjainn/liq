package com.collabnex.service.impl;

import com.collabnex.common.dto.VendorDocumentDto;
import com.collabnex.domain.vendor.*;
import com.collabnex.service.VendorDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorDocumentServiceImpl implements VendorDocumentService {

    private final VendorRepository vendorRepository;
    private final VendorDocumentRepository documentRepository;

    @Override
    public VendorDocumentDto addDocument(Long userId, VendorDocumentDto dto) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor profile not found"));

        VendorDocument document = VendorDocument.builder()
                .vendor(vendor)
                .documentType(dto.getDocumentType())
                .filePath(dto.getFilePath())
                .build();

        return toDto(documentRepository.save(document));
    }

    @Override
    public List<VendorDocumentDto> getMyDocuments(Long userId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor profile not found"));

        return documentRepository.findByVendorId(vendor.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public VendorDocumentDto updateDocument(Long userId, Long documentId, VendorDocumentDto dto) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor profile not found"));

        VendorDocument document = documentRepository
                .findByIdAndVendorId(documentId, vendor.getId())
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setDocumentType(dto.getDocumentType());
        document.setFilePath(dto.getFilePath());

        return toDto(document);
    }

    @Override
    public void deleteDocument(Long userId, Long documentId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor profile not found"));

        VendorDocument document = documentRepository
                .findByIdAndVendorId(documentId, vendor.getId())
                .orElseThrow(() -> new RuntimeException("Document not found"));

        documentRepository.delete(document);
    }

    private VendorDocumentDto toDto(VendorDocument doc) {
        return VendorDocumentDto.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .filePath(doc.getFilePath())
                .isVerified(doc.getIsVerified())
                .build();
    }
}
