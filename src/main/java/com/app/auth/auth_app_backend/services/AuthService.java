package com.app.auth.auth_app_backend.services;

import com.app.auth.auth_app_backend.dtos.UserDto;
import org.springframework.stereotype.Service;

@Service

public interface AuthService {
    UserDto registerUser(UserDto userDto);
}
