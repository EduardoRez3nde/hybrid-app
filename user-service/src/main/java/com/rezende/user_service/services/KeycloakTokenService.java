package com.rezende.user_service.services;

import com.rezende.user_service.configuration.WebClientConfig;
import com.rezende.user_service.dto.TokenResponseDTO;
import com.rezende.user_service.enums.KeycloakEndpoint;
import com.rezende.user_service.exceptions.KeycloakTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class KeycloakTokenService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String realm;

    public KeycloakTokenService(
            final WebClient webClient,
            @Value("${keycloak.admin.realm}") final String realm,
            @Value("${spring.security.oauth2.client.registration.keycloak-client.client-id}") final String clientId,
            @Value("${spring.security.oauth2.client.registration.keycloak-client.client-secret}") final String clientSecret
    ) {
        this.webClient = webClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.realm = realm;
    }

    @Cacheable(value = "keycloak-admin-token", key = "'admin-token'")
    public String getAdminToken() {

        log.info("Token not found in cache. Fetching a new one from Keycloak.");

        final TokenResponseDTO response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KeycloakEndpoint.GET_TOKEN.getPath())
                        .build(realm))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                )
                .retrieve()
                .bodyToMono(TokenResponseDTO.class)
                .block();

        if (response == null || response.access_token() == null)
            throw new KeycloakTokenException("Failed to fetch Keycloak admin token.");
        return response.access_token();
    }
}
