package com.collabnex.service.impl;

import com.collabnex.common.exception.BusinessException;
import com.collabnex.common.exception.NotFoundException;
import com.collabnex.domain.user.*;
import com.collabnex.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
public User registerLocal(String fullName, String email, String rawPassword) {

    if (userRepository.existsByEmailIgnoreCase(email)) {
        throw new BusinessException("Email already registered");
    }

    User user = User.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(rawPassword))
            .role(UserRole.CLIENT)
            .status(UserStatus.ACTIVE)
            .build();

    user = userRepository.save(user);

    UserProfile profile = UserProfile.builder()
            .user(user)
            .fullName(fullName)
            .build();

    profileRepository.save(profile);

    return user;
}

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
