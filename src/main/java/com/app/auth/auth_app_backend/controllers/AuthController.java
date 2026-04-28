package com.app.auth.auth_app_backend.controllers;

import com.app.auth.auth_app_backend.dtos.LoginRequest;
import com.app.auth.auth_app_backend.dtos.TokenResponse;
import com.app.auth.auth_app_backend.dtos.UserDto;
import com.app.auth.auth_app_backend.entities.User;
import com.app.auth.auth_app_backend.repositories.UserRepository;
import com.app.auth.auth_app_backend.security.JwtService;
import com.app.auth.auth_app_backend.services.AuthService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

private final AuthService authService;
private final AuthenticationManager authenticationManager;
private final UserRepository userRepository;
private final JwtService jwtService;
private final ModelMapper modelMapper;

@PostMapping("/login")

    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {

        //authenticate
        Authentication authenticate = authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadCredentialsException("Invalid Email"));
        if(!user.isEnabled())
            throw new DisabledException("User is Disabled");

        String accessToken = jwtService.generateAccessToken(user);

        TokenResponse tokenResponse = TokenResponse.of(accessToken, "", jwtService.getAccessTtlSeconds(), modelMapper.map(user, UserDto.class));
        return ResponseEntity.ok(tokenResponse);
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        } catch (DisabledException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        UserDto user =  authService.registerUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
