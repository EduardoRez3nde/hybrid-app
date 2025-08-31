package com.rezende.user_service.services;

import com.rezende.user_service.dto.LoginRequestDTO;
import com.rezende.user_service.dto.LoginResponseDTO;
import com.rezende.user_service.enums.KeycloakEndpoint;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Service
public class KeycloakClient {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;

    public KeycloakClient(
            @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}") final String baseUrl,
            @Value("${spring.security.oauth2.client.registration.keycloak-client.client-id}") final String clientId,
            @Value("${spring.security.oauth2.client.registration.keycloak-client.client-secret}") final String clientSecret,
            final WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public LoginResponseDTO userLogin(final LoginRequestDTO dto) {
        return webClient.post()
                .uri(KeycloakEndpoint.TOKEN.getPath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", dto.username())
                        .with("password", dto.password())
                )
                .retrieve()
                .bodyToMono(LoginResponseDTO.class)
                .block();
    }
}
