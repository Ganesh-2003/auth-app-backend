package com.app.auth.auth_app_backend.controllers;

import com.app.auth.auth_app_backend.dtos.LoginRequest;
import com.app.auth.auth_app_backend.dtos.RefreshTokenRequest;
import com.app.auth.auth_app_backend.dtos.TokenResponse;
import com.app.auth.auth_app_backend.dtos.UserDto;
import com.app.auth.auth_app_backend.entities.RefreshToken;
import com.app.auth.auth_app_backend.entities.User;
import com.app.auth.auth_app_backend.repositories.RefreshTokenRepository;
import com.app.auth.auth_app_backend.repositories.UserRepository;
import com.app.auth.auth_app_backend.security.CookieService;
import com.app.auth.auth_app_backend.security.JwtService;
import com.app.auth.auth_app_backend.services.AuthService;
import com.app.auth.auth_app_backend.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static java.util.Locale.filter;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

private final AuthService authService;
private final AuthenticationManager authenticationManager;
private final UserRepository userRepository;
private final JwtService jwtService;
private final ModelMapper modelMapper;
private final RefreshTokenRepository refreshTokenRepository;
private final CookieService cookieService;




@PostMapping("/login")

    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        //authenticate
        Authentication authenticate = authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadCredentialsException("Invalid Email"));
        if(!user.isEnabled())
            throw new DisabledException("User is Disabled");

        String jti = UUID.randomUUID().toString();
        var refreshTokenOb = RefreshToken.builder()
                        .jti(jti)
                        .user(user)
                        .createdAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                        .revoked(false)
                        .build();

        //Refresh token save--information
        refreshTokenRepository.save(refreshTokenOb);

        //access token -- generate Token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        //Use Cookie Service to attach Refresh token in Cookie
        cookieService.attachRefreshCookie(response, refreshToken, (int)jwtService.getRefreshTtlSeconds());
        cookieService.addNoStore(response);

        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTtlSeconds(), modelMapper.map(user, UserDto.class));
        return ResponseEntity.ok(tokenResponse);
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        } catch (DisabledException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    //for refreshing - access and refresh token renew

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String refreshToken = readRefreshTokenFromRequest(body, request).orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));

        if(!jwtService.isRefreshToken(refreshToken)){
            throw new BadCredentialsException("Invalid Refresh Token Type");
        }

        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserId(refreshToken);

        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti).orElseThrow(() -> new BadCredentialsException("Refresh Token Not Found"));

        if(storedRefreshToken.isRevoked()) {
            throw new BadCredentialsException("Refresh token expired or revoked");
        }

        if(storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        if(!storedRefreshToken.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Refresh token doesn't not belong to this User");
        }

        //refresh token to rotate
        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);

        User user1 = storedRefreshToken.getUser();

        var newRefreshTokenOb = RefreshToken.builder()
                .jti(newJti)
                .user(user1)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshTokenOb);

        String accessToken = jwtService.generateAccessToken(user1);
        String newRefreshToken = jwtService.generateRefreshToken(user1, newRefreshTokenOb.getJti());

        cookieService.attachRefreshCookie(response, newRefreshToken, (int)jwtService.getRefreshTtlSeconds());
        cookieService.addNoStore(response);
        return ResponseEntity.ok(TokenResponse.of(accessToken, newRefreshToken, jwtService.getAccessTtlSeconds(), modelMapper.map(user1, UserDto.class)));
    }

    //This method will read refreshToken from body or request.
    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {

        // 1. Prefer Reading from Cookie
        if(request.getCookies() != null) {
            Optional<String> fromCookie = Arrays.stream(
                    request.getCookies()
            ).filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v-> !v.isBlank())
                    .findFirst();

            if (fromCookie.isPresent()) {
                return fromCookie;
            }

        }

        // 2 Body
        if(body!=null && body.refreshToken() != null && !body.refreshToken().isBlank())
        {
            return Optional.of(body.refreshToken());
        }

        // 3 Custom Header
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            return Optional.of(refreshHeader);
        }

        //Authorization = Bearer <token>
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {
                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();
    }

    @PostMapping("/logout")
    public ResponseEntity<TokenResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        readRefreshTokenFromRequest(null, request).ifPresent(refreshToken -> {
            try {
                if(jwtService.isRefreshToken(refreshToken)) {
                    String jti = jwtService.getJti(refreshToken);
                    refreshTokenRepository.findByJti(jti).ifPresent(token -> {
                        token.setRevoked(true);
                        refreshTokenRepository.save(token);
                    });
                }
            }
            catch(JwtException e) {

            }
        });

        cookieService.clearRefreshCookie(response);
        cookieService.addNoStore(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        UserDto user =  authService.registerUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
