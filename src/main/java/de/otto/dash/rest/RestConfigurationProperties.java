package de.otto.dash.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("otto-dash")
public record RestConfigurationProperties(
        Endpoint endpoint
) {
    public record Endpoint(
            String url,
            int connectTimeout,
            int readTimeout
    ) {
    }
}
