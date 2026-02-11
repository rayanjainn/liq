package com.collabnex.service;


import com.collabnex.common.dto.VendorTeamDto;
import com.collabnex.common.dto.VendorTeamRequest;

import java.util.List;

public interface VendorTeamService {

    VendorTeamDto addTeamMember(Long userId, VendorTeamRequest request);

    List<VendorTeamDto> getMyTeam(Long userId);

    void deleteTeamMember(Long userId, Long teamId);
}
