package com.app.auth.auth_app_backend.services.Impl;

import com.app.auth.auth_app_backend.dtos.UserDto;
import com.app.auth.auth_app_backend.services.AuthService;
import com.app.auth.auth_app_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto userDto) {

        //logic verify email
        //logic verify password
        //default roles
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserDto userDto1 = userService.createUser(userDto);
        return userDto1;
    }
}
