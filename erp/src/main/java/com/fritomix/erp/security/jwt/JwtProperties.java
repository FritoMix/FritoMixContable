package com.fritomix.erp.security.jwt;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtProperties {

    /**
     * Clave secreta utilizada para firmar los JWT.
     */
    private String secret;

    /**
     * Tiempo de expiración del Access Token (ms).
     */
    private Long accessTokenExpiration;

    /**
     * Tiempo de expiración del Refresh Token (ms).
     */
    private Long refreshTokenExpiration;

}