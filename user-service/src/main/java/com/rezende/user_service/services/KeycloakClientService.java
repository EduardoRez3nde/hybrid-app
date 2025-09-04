package com.rezende.user_service.services;

import com.rezende.user_service.dto.LoginRequestDTO;
import com.rezende.user_service.dto.LoginResponseDTO;
import com.rezende.user_service.dto.RegisterUser;
import com.rezende.user_service.dto.UserPayloadDTO;
import com.rezende.user_service.enums.KeycloakEndpoint;
import com.rezende.user_service.exceptions.KeycloakUserCreationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Getter
@Service
@Slf4j
public class KeycloakClientService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String realm;
    private final KeycloakTokenService keycloakTokenService;

    public KeycloakClientService(
            final WebClient webClient,
            final KeycloakTokenService keycloakTokenService,
            @Value("${keycloak.admin.realm}") final String realm,
            @Value("${spring.security.oauth2.client.registration.keycloak-client.client-id}") final String clientId,
            @Value("${spring.security.oauth2.client.registration.keycloak-client.client-secret}") final String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webClient = webClient;
        this.keycloakTokenService = keycloakTokenService;
        this.realm = realm;
    }

    public LoginResponseDTO userLogin(final LoginRequestDTO dto) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KeycloakEndpoint.GET_TOKEN.getPath())
                        .build(realm))
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

    public String createUserInKeycloak(final RegisterUser user) {

        log.info("Attempting to create user in Keycloak for email: {}", user.getEmail());

        final String adminToken = keycloakTokenService.getAdminToken();
        final UserPayloadDTO userPayload = UserPayloadDTO.of(user);

        log.debug("User payload to be sent to Keycloak: {}", userPayload);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(KeycloakEndpoint.CREATE_USER.getPath())
                        .build(realm))
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPayload)
                .exchangeToMono(response -> {
                    final String location = response.headers().asHttpHeaders().getFirst("Location");

                    if (response.statusCode().is2xxSuccessful() && location != null) {
                        final String newUserId = location.substring(location.lastIndexOf("/") + 1);
                        log.info("User created successfully in Keycloak with ID: {}", newUserId);
                        return Mono.just(newUserId);
                    }
                    return response
                            .bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error("Error creating user in Keycloak. Status: {}, Response: {}",
                                        response.statusCode().value(), errorBody);
                                return Mono.error(new KeycloakUserCreationException("Error creating user in Keycloak. Check logs for details."));
                            });
                })
                .doOnError(error -> {
                    log.error("A network error occurred while trying to create user in Keycloak.", error);
                })
                .block();
    }

    public void deleteUserInKeycloak(final String userId) {
        webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(KeycloakEndpoint.DELETE_USER.getPath())
                        .build(realm, userId))
                .header("Authorization", "Bearer " + keycloakTokenService.getAdminToken())
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void sendVerificationEmail(final String userId) {
        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(KeycloakEndpoint.SEND_VERIFY_EMAIL.getPath())
                        .build(realm, userId))
                .header("Authorization", "Bearer " + keycloakTokenService.getAdminToken())
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}