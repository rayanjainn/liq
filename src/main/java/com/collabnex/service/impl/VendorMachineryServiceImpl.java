package com.collabnex.service.impl;

import com.collabnex.common.dto.VendorMachineryDto;
import com.collabnex.common.exception.NotFoundException;
import com.collabnex.domain.vendor.*;
import com.collabnex.service.VendorMachineryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorMachineryServiceImpl implements VendorMachineryService {

    private final VendorRepository vendorRepository;
    private final VendorMachineryRepository machineryRepository;

    @Override
    public VendorMachineryDto addMachinery(Long userId, VendorMachineryDto dto) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor not found"));

        VendorMachinery machinery = VendorMachinery.builder()
                .vendor(vendor)
                .machineName(dto.getMachineName())
                .machineType(dto.getMachineType())
                .manufacturer(dto.getManufacturer())
                .quantity(dto.getQuantity())
                .capabilities(dto.getCapabilities())
                .isOperational(dto.getIsOperational())
                .build();

        machineryRepository.save(machinery);
        return toDto(machinery);
    }

    @Override
    public List<VendorMachineryDto> getMyMachineries(Long userId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Vendor not found"));

        return machineryRepository.findAllByVendorId(vendor.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public VendorMachineryDto updateMachinery(
            Long userId,
            Long machineryId,
            VendorMachineryDto dto
    ) {

        VendorMachinery machinery = machineryRepository.findById(machineryId)
                .orElseThrow(() -> new NotFoundException("Machinery not found"));

        if (dto.getMachineName() != null) machinery.setMachineName(dto.getMachineName());
        if (dto.getMachineType() != null) machinery.setMachineType(dto.getMachineType());
        if (dto.getManufacturer() != null) machinery.setManufacturer(dto.getManufacturer());
        if (dto.getQuantity() != null) machinery.setQuantity(dto.getQuantity());
        if (dto.getCapabilities() != null) machinery.setCapabilities(dto.getCapabilities());
        if (dto.getIsOperational() != null) machinery.setIsOperational(dto.getIsOperational());

        return toDto(machinery);
    }

    @Override
    public void deleteMachinery(Long userId, Long machineryId) {
        machineryRepository.deleteById(machineryId);
    }

    private VendorMachineryDto toDto(VendorMachinery m) {
        return VendorMachineryDto.builder()
                .id(m.getId())
                .machineName(m.getMachineName())
                .machineType(m.getMachineType())
                .manufacturer(m.getManufacturer())
                .quantity(m.getQuantity())
                .capabilities(m.getCapabilities())
                .isOperational(m.getIsOperational())
                .build();
    }
}
