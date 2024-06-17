package br.com.bbtecno.desafio.core.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RequiredArgsConstructor
public class CachingOAuth2AuthorizedClientService implements ReactiveOAuth2AuthorizedClientService {

    private final CacheManager cacheManager;

    @Override
    public <T extends OAuth2AuthorizedClient> Mono<T> loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return Mono.fromCallable(() -> {
            OAuth2AuthorizedClientId clientId = new OAuth2AuthorizedClientId(clientRegistrationId, principalName);
            return (T) cacheManager.getCache("oauth2Tokens").get(clientId, OAuth2AuthorizedClient.class);
        });
    }


    @Override
    public Mono<Void> saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        return Mono.fromRunnable(() -> {
            OAuth2AuthorizedClientId clientId = new OAuth2AuthorizedClientId(authorizedClient.getClientRegistration().getRegistrationId(), principal.getName());
            Objects.requireNonNull(cacheManager.getCache("oauth2Tokens")).put(clientId, authorizedClient);
        });
    }

    @Override
    public Mono<Void> removeAuthorizedClient(String clientRegistrationId, String principalName) {
        return Mono.fromRunnable(() -> {
            OAuth2AuthorizedClientId clientId = new OAuth2AuthorizedClientId(clientRegistrationId, principalName);
            Objects.requireNonNull(cacheManager.getCache("oauth2Tokens")).evict(clientId);
        });
    }
}

