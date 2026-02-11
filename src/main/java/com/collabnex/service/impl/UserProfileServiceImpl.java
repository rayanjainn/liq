package com.collabnex.service.impl;

import com.collabnex.common.dto.UserProfileDto;
import com.collabnex.common.exception.NotFoundException;
import com.collabnex.domain.user.*;
import com.collabnex.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository profileRepository;
    
@Override
public UserProfileDto getMyProfile(Long userId) {

    UserProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Profile not found"));

    return toDto(profile);
}

@Override
public UserProfileDto updateMyProfile(
        Long userId,
        String fullName,
        String phone,
        String organizationName,
        String organizationType
) {
    UserProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Profile not found"));

    if (fullName != null) profile.setFullName(fullName);
    if (phone != null) profile.setPhone(phone);
    if (organizationName != null) profile.setOrganizationName(organizationName);

    if (organizationType != null) {
        profile.setOrganizationType(
                OrganizationType.valueOf(organizationType.toUpperCase())
        );
    }

    profileRepository.save(profile);

    return toDto(profile);
}

private UserProfileDto toDto(UserProfile profile) {
    return UserProfileDto.builder()
            .id(profile.getId())
            .fullName(profile.getFullName())
            .phone(profile.getPhone())
            .organizationName(profile.getOrganizationName())
            .organizationType(profile.getOrganizationType())
            .build();
}

}
