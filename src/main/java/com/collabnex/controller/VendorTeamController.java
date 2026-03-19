package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.VendorTeamDto;
import com.collabnex.common.dto.VendorTeamRequest;
import com.collabnex.config.CustomUserDetails;
import com.collabnex.service.VendorTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vendor team management.
 * All endpoints require authentication (enforced by SecurityConfig's anyRequest().authenticated()).
 */
@RestController
@RequestMapping("/api/vendor/team")
@RequiredArgsConstructor
public class VendorTeamController {

    private final VendorTeamService teamService;

    /**
     * POST /api/vendor/team
     * Access: Authenticated
     * Description: Add a team member to the vendor's team.
     *
     * @param currentUser the authenticated user (from JWT)
     * @param request     team member details
     * @return the created VendorTeamDto
     */
    @PostMapping
    public ApiResponse<VendorTeamDto> add(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody VendorTeamRequest request
    ) {
        return ApiResponse.ok(
                teamService.addTeamMember(currentUser.getId(), request)
        );
    }

    /**
     * GET /api/vendor/team
     * Access: Authenticated
     * Description: List all team members for the authenticated vendor.
     *
     * @param currentUser the authenticated user
     * @return list of VendorTeamDto objects
     */
    @GetMapping
    public ApiResponse<List<VendorTeamDto>> list(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ApiResponse.ok(
                teamService.getMyTeam(currentUser.getId())
        );
    }

    /**
     * DELETE /api/vendor/team/{teamId}
     * Access: Authenticated
     * Description: Remove a team member by their ID.
     *
     * @param currentUser the authenticated user
     * @param teamId      the team member's ID to delete
     * @return empty success response
     */
    @DeleteMapping("/{teamId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable("teamId") Long teamId
    ) {
        teamService.deleteTeamMember(currentUser.getId(), teamId);
        return ApiResponse.ok(null);
    }
}
