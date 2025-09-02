package com.rezende.user_service.services;

import com.rezende.user_service.dto.LoginRequestDTO;
import com.rezende.user_service.dto.LoginResponseDTO;
import com.rezende.user_service.dto.RegisterUser;
import com.rezende.user_service.dto.UserPayloadDTO;
import com.rezende.user_service.enums.KeycloakEndpoint;
import com.rezende.user_service.exceptions.KeycloakUserCreationException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

@Getter
@Service
public class KeycloakClientService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String adminApiBaseUrl;

    public KeycloakClientService(
            @Value("${keycloak.admin.realm}") final String realm,
            @Value("${keycloak.admin.server-url}") final String serverUrl,
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
        this.adminApiBaseUrl = UriComponentsBuilder
                .fromUriString(serverUrl)
                .pathSegment("admin", "realms", realm)
                .toUriString();
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

    public String createUserInKeycloak(final RegisterUser user) {

        final String adminToken = getAdminToken();
        final UserPayloadDTO userPayload = UserPayloadDTO.of(user);

        return webClient.post()
                .uri(adminApiBaseUrl + "/users")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPayload)
                .exchangeToMono(response -> {
                    final String location = response.headers().asHttpHeaders().getFirst("Location");

                    if (response.statusCode().is2xxSuccessful() && location != null)
                        return Mono.just(location.substring(location.lastIndexOf("/") + 1));

                    return response
                            .bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(
                                    new KeycloakUserCreationException("Error creating user in Keycloak"))
                            );
                })
                .block();
    }

    public void sendVerificationEmail(final String userId) {
        webClient.put()
                .uri(this.adminApiBaseUrl + "/users/" + userId + "/send-verify-email")
                .header("Authorization", "Bearer " + getAdminToken())
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private String getAdminToken() {
        return webClient.post()
                .uri(KeycloakEndpoint.TOKEN.getPath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                )
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .block();
    }
}
