package com.stephenusselman.incidentservice.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for OpenAI client integration.
 */
@Configuration
public class OpenAIConfig {

    /**
     * Provides a configured OpenAI client using environment-based configuration.
     *
     * @return an initialized {@link OpenAIClient}
     */
    @Bean
    public OpenAIClient openAIClient() {
        return OpenAIOkHttpClient.fromEnv();
    }
}