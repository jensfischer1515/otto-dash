package de.otto.dash.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@ConstructorBinding
@ConfigurationProperties("otto-dash")
public record RestConfigurationProperties(
        Endpoint endpoint
) {
    public record Endpoint(
            String url,
            Duration connectTimeout,
            Duration readTimeout
    ) {
    }
}
