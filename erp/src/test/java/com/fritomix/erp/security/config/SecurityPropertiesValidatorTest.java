package com.fritomix.erp.security.config;

import com.fritomix.erp.security.jwt.JwtProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SecurityPropertiesValidatorTest {

    private static final String WEAK_SECRET = "ZGV2LW9ubHktc2VjcmV0LW5vdC12YWxpZC1mb3ItcHJvZA==";
    private static final String UNCONFIGURED_SECRET = "CHANGE_ME_IN_PRODUCTION";
    private static final String STRONG_SECRET = "x7kP2mQ9vL4nR8sT1uW6yZ3aB5cD0eFgHjK1lM2nO3pQ4rS5tU6vW7xY8z";

    private SecurityPropertiesValidator validator(String secret, String dbPassword, String profiles) {
        JwtProperties props = new JwtProperties();
        props.setSecret(secret);
        return new SecurityPropertiesValidator(props, dbPassword, profiles);
    }

    @Test
    void validate_shouldThrowWithWeakJwtSecret() {
        assertThrows(IllegalStateException.class,
                () -> validator(WEAK_SECRET, "strong-db-pass", "prod").validate());
    }

    @Test
    void validate_shouldThrowWithUnconfiguredJwtSecret() {
        assertThrows(IllegalStateException.class,
                () -> validator(UNCONFIGURED_SECRET, "strong-db-pass", "prod").validate());
        assertThrows(IllegalStateException.class,
                () -> validator(UNCONFIGURED_SECRET, "strong-db-pass", "dev").validate());
    }

    @Test
    void validate_shouldThrowWhenJwtSecretMissing() {
        assertThrows(IllegalStateException.class,
                () -> validator("", "strong-db-pass", "dev").validate());
        assertThrows(IllegalStateException.class,
                () -> validator(null, "strong-db-pass", "dev").validate());
    }

    @Test
    void validate_shouldThrowInProdWithWeakDbPassword() {
        assertThrows(IllegalStateException.class,
                () -> validator(STRONG_SECRET, "123456", "prod").validate());
    }

    @Test
    void validate_shouldPassInProdWithStrongSecrets() {
        assertDoesNotThrow(
                () -> validator(STRONG_SECRET, "strong-db-pass", "prod,prod-db").validate());
    }

    @Test
    void validate_shouldPassOutsideProdWithStrongSecret() {
        assertDoesNotThrow(() -> validator(STRONG_SECRET, "123456", "dev").validate());
        assertDoesNotThrow(() -> validator(STRONG_SECRET, "123456", "").validate());
    }
}