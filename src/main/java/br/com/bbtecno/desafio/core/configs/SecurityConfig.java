package br.com.bbtecno.desafio.core.configs;

import br.com.bbtecno.desafio.core.cache.CachingOAuth2AuthorizedClientService;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(authorize -> authorize
                        .anyExchange()
                        .authenticated())
                .oauth2Login(withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
        ;
        return http.build();
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientService authorizedClientService(CacheManager cacheManager) {
        return new CachingOAuth2AuthorizedClientService(cacheManager);
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository(ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }
}
