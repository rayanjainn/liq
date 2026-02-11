package com.collabnex.service;

import com.collabnex.common.dto.VendorMachineryDto;

import java.util.List;

public interface VendorMachineryService {

    VendorMachineryDto addMachinery(Long userId, VendorMachineryDto dto);

    List<VendorMachineryDto> getMyMachineries(Long userId);

    VendorMachineryDto updateMachinery(Long userId, Long machineryId, VendorMachineryDto dto);

    void deleteMachinery(Long userId, Long machineryId);
}
