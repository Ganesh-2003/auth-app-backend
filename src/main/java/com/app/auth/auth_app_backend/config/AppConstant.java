package com.app.auth.auth_app_backend.config;

public class AppConstant {

    public static final String[] AUTH_PUBLIC_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/v1/auth/**" //Ensure Login, Register, logout, refresh
    };
}
