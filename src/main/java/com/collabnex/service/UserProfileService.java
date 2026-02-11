package com.collabnex.service;

import com.collabnex.common.dto.UserProfileDto;

public interface UserProfileService {

    UserProfileDto getMyProfile(Long userId);

    UserProfileDto updateMyProfile(
            Long userId,
            String fullName,
            String phone,
            String organizationName,
            String organizationType
    );
}
