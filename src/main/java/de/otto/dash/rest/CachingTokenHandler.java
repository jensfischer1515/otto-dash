package de.otto.dash.rest;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;
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
        Token token = tokenCache.get(scopes());
        return token.accessToken();
    }

    // Evict based on a varying expiration policy
    private static class TokenExpiry implements Expiry<String, Token> {

        private final Duration expiryBuffer;

        private TokenExpiry(Duration expiryBuffer) {
            this.expiryBuffer = expiryBuffer;
        }

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
