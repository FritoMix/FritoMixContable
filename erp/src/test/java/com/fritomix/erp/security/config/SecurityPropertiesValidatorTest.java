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
    private static final String STRONG_DB_PASS = "strong-db-pass";
    private static final String PLACEHOLDER = "CHANGE_ME_IN_PRODUCTION";

    private SecurityPropertiesValidator validator(String secret, String dbPassword, String profiles) {
        return validator(secret, dbPassword, STRONG_DB_PASS, profiles);
    }

    private SecurityPropertiesValidator validator(String secret, String dbPassword,
                                                  String flywayPassword, String profiles) {
        JwtProperties props = new JwtProperties();
        props.setSecret(secret);
        return new SecurityPropertiesValidator(props, dbPassword, flywayPassword, profiles);
    }

    @Test
    void validate_shouldThrowWithWeakJwtSecret() {
        assertThrows(IllegalStateException.class,
                () -> validator(WEAK_SECRET, STRONG_DB_PASS, "prod").validate());
    }

    @Test
    void validate_shouldThrowWithUnconfiguredJwtSecret() {
        assertThrows(IllegalStateException.class,
                () -> validator(UNCONFIGURED_SECRET, STRONG_DB_PASS, "prod").validate());
        assertThrows(IllegalStateException.class,
                () -> validator(UNCONFIGURED_SECRET, STRONG_DB_PASS, "dev").validate());
    }

    @Test
    void validate_shouldThrowWhenJwtSecretMissing() {
        assertThrows(IllegalStateException.class,
                () -> validator("", STRONG_DB_PASS, "dev").validate());
        assertThrows(IllegalStateException.class,
                () -> validator(null, STRONG_DB_PASS, "dev").validate());
    }

    @Test
    void validate_shouldThrowInProdWithWeakDbPassword() {
        assertThrows(IllegalStateException.class,
                () -> validator(STRONG_SECRET, "123456", "prod").validate());
    }

    /**
     * docker-compose falls back to this placeholder when .env is missing, so the database
     * would be protected by a publicly known constant and the application would happily
     * authenticate against it.
     */
    @Test
    void validate_shouldThrowInProdWithComposePlaceholderDbPassword() {
        assertThrows(IllegalStateException.class,
                () -> validator(STRONG_SECRET, PLACEHOLDER, "prod").validate());
        assertThrows(IllegalStateException.class,
                () -> validator(STRONG_SECRET, STRONG_DB_PASS, PLACEHOLDER, "prod").validate());
    }

    @Test
    void validate_shouldThrowInProdWithEmptyPasswords() {
        assertThrows(IllegalStateException.class,
                () -> validator(STRONG_SECRET, "", "prod").validate());
        assertThrows(IllegalStateException.class,
                () -> validator(STRONG_SECRET, STRONG_DB_PASS, "", "prod").validate());
    }

    @Test
    void validate_shouldPassInProdWithStrongSecrets() {
        assertDoesNotThrow(
                () -> validator(STRONG_SECRET, STRONG_DB_PASS, "prod,prod-db").validate());
        assertDoesNotThrow(
                () -> validator(STRONG_SECRET, STRONG_DB_PASS, STRONG_DB_PASS, "prod").validate());
    }

    @Test
    void validate_shouldPassOutsideProdWithStrongSecret() {
        assertDoesNotThrow(() -> validator(STRONG_SECRET, "123456", "dev").validate());
        assertDoesNotThrow(() -> validator(STRONG_SECRET, "123456", "").validate());
    }
}
