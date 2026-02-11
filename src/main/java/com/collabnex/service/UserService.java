package com.collabnex.service;

import com.collabnex.domain.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User registerLocal(String fullName, String email, String rawPassword);
    User getById(Long id);
    User getByEmail(String email);
}
