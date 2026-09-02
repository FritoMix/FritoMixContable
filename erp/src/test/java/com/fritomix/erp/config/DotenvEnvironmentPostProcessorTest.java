package com.fritomix.erp.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DotenvEnvironmentPostProcessorTest {

    @Test
    void loadsMailApiKeyFromDotenv() {
        DotenvEnvironmentPostProcessor processor = new DotenvEnvironmentPostProcessor();
        MockEnvironment environment = new MockEnvironment();
        processor.postProcessEnvironment(environment, null);

        String apiKey = environment.getProperty("MAIL_API_KEY");
        System.out.println("Resolved MAIL_API_KEY -> " + (apiKey == null ? "NULL" : apiKey.substring(0, 12) + "..."));
        assertTrue(apiKey != null && !apiKey.isBlank(),
                "MAIL_API_KEY debe resolverse desde .env");
    }
}
