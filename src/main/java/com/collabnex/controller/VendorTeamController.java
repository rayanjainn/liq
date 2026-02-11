package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.VendorTeamDto;
import com.collabnex.common.dto.VendorTeamRequest;
import com.collabnex.domain.user.User;
import com.collabnex.service.VendorTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/team")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VENDOR')")
public class VendorTeamController {

    private final VendorTeamService teamService;

    @PostMapping
    public ApiResponse<VendorTeamDto> add(
            @AuthenticationPrincipal User user,
            @RequestBody VendorTeamRequest request
    ) {
        return ApiResponse.ok(
                teamService.addTeamMember(user.getId(), request)
        );
    }

    @GetMapping
    public ApiResponse<List<VendorTeamDto>> list(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(
                teamService.getMyTeam(user.getId())
        );
    }

    @DeleteMapping("/{teamId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable("teamId") Long teamId
    ) {
        teamService.deleteTeamMember(user.getId(), teamId);
        return ApiResponse.ok(null);
    }
}
