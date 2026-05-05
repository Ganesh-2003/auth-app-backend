package com.app.auth.auth_app_backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpHeaders.PRAGMA;

@Service
@Getter
public class CookieService {

    private final  String refreshTokenCookieName;
    private final boolean cookieHttpOnly;
    private final boolean cookieSecure;
    private final String cookieDomain;
    private final String cookieSameSite;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public CookieService(
            @Value("${security.jwt.refresh-token-cookie-name}") String refreshTokenCookieName,
            @Value("${security.jwt.cookie-http-only}") boolean cookieHttpOnly,
            @Value("${security.jwt.cookie-secure}") boolean cookieSecure,
            @Value("${security.jwt.cookie-domain}") String cookieDomain,
            @Value("${security.jwt.cookie-same-site}") String cookieSameSite
    ) {
      this.refreshTokenCookieName = refreshTokenCookieName;
      this.cookieHttpOnly = cookieHttpOnly;
      this.cookieSecure = cookieSecure;
      this.cookieDomain = cookieDomain;
      this.cookieSameSite = cookieSameSite;
    }

    //create method to attach cookie to Response
    public void attachRefreshCookie(HttpServletResponse response, String value, int maxAge) {

        logger.info("Attching Cookie" , refreshTokenCookieName, value);

        var ResponseCookieBuilder = ResponseCookie.from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAge)
                .sameSite(cookieSameSite);

        if(cookieDomain != null && !cookieDomain.isBlank()) {
            ResponseCookieBuilder.domain(cookieDomain);
        }

        ResponseCookie responseCookie = ResponseCookieBuilder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    //Clear Refresh Cookie
    public void clearRefreshCookie(HttpServletResponse response) {
        var builder = ResponseCookie.from(refreshTokenCookieName, "")
                .maxAge(0)
                .httpOnly(cookieHttpOnly)
                .path("/")
                .sameSite(cookieSameSite)
                .secure(cookieSecure);

        if(cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie responseCookie = builder.build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    public void addNoStore(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("PRAGMA", "no-cache");
    }

}
