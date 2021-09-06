package de.otto.dash.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

record Token(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("scope") String scope,
        @JsonProperty("token_type") String tokenType
) {
}
