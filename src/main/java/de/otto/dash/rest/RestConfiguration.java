package de.otto.dash.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RestConfigurationProperties.class)
public class RestConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(RestConfigurationProperties properties) {
        var requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(Math.toIntExact(properties.endpoint().connectTimeout().toMillis()));
        requestFactory.setConnectTimeout(Math.toIntExact(properties.endpoint().connectTimeout().toMillis()));
        requestFactory.setReadTimeout(Math.toIntExact(properties.endpoint().readTimeout().toMillis()));

        return new BufferingClientHttpRequestFactory(requestFactory);
    }

    @Bean
    public RestTemplateCustomizer timeoutCustomizer(ClientHttpRequestFactory requestFactory) {
        return restTemplate -> restTemplate.setRequestFactory(requestFactory);
    }

    @Bean
    public RestTemplateCustomizer loggingCustomizer(Logbook logbook) {
        return restTemplate -> restTemplate.getInterceptors().add(new LogbookClientHttpRequestInterceptor(logbook));
    }

    @Bean
    public TokenHandler tokenHandler(RestTemplateBuilder restTemplateBuilder, OAuth2ClientProperties oauth2ClientProperties) {
        return new CachingTokenHandler(restTemplateBuilder, oauth2ClientProperties, applicationName, Duration.ofSeconds(120));
    }
}
