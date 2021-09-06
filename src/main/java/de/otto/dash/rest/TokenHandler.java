package de.otto.dash.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TokenHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenHandler.class);

    protected final RestTemplate restTemplate;
    protected final OAuth2ClientProperties.Registration registration;
    protected final OAuth2ClientProperties.Provider provider;

    protected TokenHandler(
            RestTemplateBuilder restTemplateBuilder,
            OAuth2ClientProperties clientProperties,
            String apiIdentifier
    ) {
        this.registration = clientProperties.getRegistration().get(apiIdentifier);
        this.provider = clientProperties.getProvider().get(apiIdentifier);
        this.restTemplate = restTemplateBuilder
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent())
                .build();
    }

    public String userAgent() {
        return registration.getClientName();
    }

    public String getBearerToken() {
        Token token = executeTokenExchange(scopes());
        return token.accessToken();
    }

    protected Token executeTokenExchange(String scopes) {
        LOGGER.debug("Exchanging client credentials with OAuth2 token from {}", provider.getTokenUri());

        var request = RequestEntity
                .post(provider.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData(scopes));

        return restTemplate.exchange(request, Token.class).getBody();
    }

    protected String scopes() {
        return String.join(" ", registration.getScope());
    }

    protected MultiValueMap<String, String> formData(String scopes) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", registration.getAuthorizationGrantType());
        form.add("client_id", registration.getClientId());
        form.add("client_secret", registration.getClientSecret());
        form.add("scope", scopes);
        return form;
    }
}
