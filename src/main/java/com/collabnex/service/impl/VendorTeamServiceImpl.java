package com.collabnex.service.impl;


import com.collabnex.common.dto.VendorTeamDto;
import com.collabnex.common.dto.VendorTeamRequest;
import com.collabnex.domain.vendor.Vendor;
import com.collabnex.domain.vendor.VendorRepository;
import com.collabnex.domain.vendor.VendorTeam;
import com.collabnex.domain.vendor.VendorTeamRepository;
import com.collabnex.service.VendorTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VendorTeamServiceImpl implements VendorTeamService {

    private final VendorRepository vendorRepository;
    private final VendorTeamRepository teamRepository;

    @Override
    public VendorTeamDto addTeamMember(Long userId, VendorTeamRequest request) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        VendorTeam team = VendorTeam.builder()
                .vendor(vendor)
                .role(request.getRole())
                .count(request.getCount())
                .build();

        teamRepository.save(team);

        return toDto(team);
    }

    @Override
    public List<VendorTeamDto> getMyTeam(Long userId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        return teamRepository.findAllByVendorId(vendor.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void deleteTeamMember(Long userId, Long teamId) {

        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        VendorTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team record not found"));

        if (!team.getVendor().getId().equals(vendor.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        teamRepository.delete(team);
    }

    private VendorTeamDto toDto(VendorTeam team) {
        return VendorTeamDto.builder()
                .id(team.getId())
                .role(team.getRole())
                .count(team.getCount())
                .build();
    }
}
