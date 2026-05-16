package com.app.auth.auth_app_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Application built by Ganesh",
                description = "Generic Auth App that can be used with any application",
                contact = @Contact(
                        name = "Ganesh Puppala",
                        url = "https://github.com/Ganesh-2003",
                        email = "ganeeshpuppala@gmail.com"
                ),
                version = "1.0",
                summary = "Built a secure and scalable Authentication Application using modern authentication and authorization mechanisms. The application supports user registration, login, JWT-based authentication, refresh token management, OAuth2 social login integration, and role-based access control. Designed with a reusable architecture so it can be integrated as a centralized authentication service for other applications. Implemented secure token handling using HTTP-only cookies, refresh token rotation, and session management to enhance application security and user experience."
        ),
        security = {
                @SecurityRequirement(
                        name="bearerAuth"
                )
        }
)

@SecurityScheme(
        name = "bearerAuth",
        type  = SecuritySchemeType.HTTP,
        scheme = "bearer", //Authorization: Bearer,
        bearerFormat = "JWT"
)
public class APIDocConfig  {

}
