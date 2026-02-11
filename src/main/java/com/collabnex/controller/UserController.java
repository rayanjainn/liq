package com.collabnex.controller;

import com.collabnex.common.dto.ApiResponse;
import com.collabnex.common.dto.UserProfileDto;
import com.collabnex.service.UserProfileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService profileService;
@GetMapping("/me")
public ResponseEntity<ApiResponse<UserProfileDto>> getMyProfile(
        @AuthenticationPrincipal(expression = "id") Long userId
) {
    return ResponseEntity.ok(
            ApiResponse.ok(profileService.getMyProfile(userId))
    );
}

@PutMapping("/me")
public ResponseEntity<ApiResponse<UserProfileDto>> updateMyProfile(
        @AuthenticationPrincipal(expression = "id") Long userId,
        @RequestBody UpdateProfileRequest req
) {
    return ResponseEntity.ok(
            ApiResponse.ok(
                    profileService.updateMyProfile(
                            userId,
                            req.fullName,
                            req.phone,
                            req.organizationName,
                            req.organizationType
                    )
            )
    );
}


    @Data
    public static class UpdateProfileRequest {
        private String fullName;
        private String phone;
        private String organizationName;
        private String organizationType;
    }
}
