package de.otto.dash.rest;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CachingTokenHandler extends TokenHandler {

    private final LoadingCache<String, Token> tokenCache;

    public CachingTokenHandler(
            RestTemplateBuilder restTemplateBuilder,
            OAuth2ClientProperties clientProperties,
            String apiIdentifier,
            Duration tokenExpiryBuffer
    ) {
        super(restTemplateBuilder, clientProperties, apiIdentifier);
        this.tokenCache = Caffeine.newBuilder()
                .expireAfter(new TokenExpiry(tokenExpiryBuffer))
                .build(super::executeTokenExchange);
    }

    @Override
    public String getBearerToken() {
        Token token = Optional.ofNullable(tokenCache.get(scopes()))
                .orElseThrow(() -> new IllegalStateException("No cached token found"));
        return token.accessToken();
    }

    // Evict based on a varying expiration policy
    private record TokenExpiry(Duration expiryBuffer) implements Expiry<String, Token> {

        @Override
        public long expireAfterCreate(String scopes, Token token, long currentTime) {
            return TimeUnit.SECONDS.toNanos(token.expiresIn() - expiryBuffer.toSeconds());
        }

        @Override
        public long expireAfterUpdate(String scopes, Token token, long currentTime, long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(String scopes, Token token, long currentTime, long currentDuration) {
            return currentDuration;
        }
    }
}
