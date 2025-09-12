package com.rezende.user_service.services;

import com.rezende.user_service.dto.TokenResponseDTO;
import com.rezende.user_service.exceptions.KeycloakTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakTokenServiceTests {

    @Mock
    private WebClient webClient;

    @Mock
    private RequestBodyUriSpec uriSpec;

    @Mock
    private RequestBodySpec bodySpec;

    @Mock
    private RequestHeadersSpec<?> headersSpec;

    @Mock
    private ResponseSpec responseSpec;

    private KeycloakTokenService tokenService;

    @BeforeEach
    void setup() {
        String clientId = "test-client";
        String clientSecret = "secret";
        String realm = "test-realm";
        tokenService = new KeycloakTokenService(webClient, realm, clientId, clientSecret);
    }

    @Test
    @DisplayName("Deve retornar token quando Keycloak responde corretamente")
    void shouldReturnTokenSuccessfully() {
        final TokenResponseDTO tokenResponse = new TokenResponseDTO(
                "mocked-access-token",
                3600,
                7200,
                "bearer",
                0,
                "openid"
        );

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any(BodyInserters.FormInserter.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TokenResponseDTO.class)).thenReturn(Mono.just(tokenResponse));

        final String token = tokenService.getAdminToken();

        assertNotNull(token);
        assertEquals("mocked-access-token", token);

        verify(webClient).post();
        verify(uriSpec).uri(any(Function.class));
        verify(bodySpec).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        verify(bodySpec).body(any());
        verify(headersSpec).retrieve();
        verify(responseSpec).bodyToMono(TokenResponseDTO.class);
    }

    @Test
    @DisplayName("Deve lançar exceção quando Keycloak retorna nulo")
    void shouldThrowExceptionWhenTokenIsNull() {

        final TokenResponseDTO token = new TokenResponseDTO(
                null,
                3600,
                7200,
                "bearer",
                0,
                "openid"
        );

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any(BodyInserters.FormInserter.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TokenResponseDTO.class)).thenReturn(Mono.just(token));

        assertThrows(KeycloakTokenException.class, () -> tokenService.getAdminToken());
    }

    @Test
    @DisplayName("Deve lançar exceção quando Keycloak não retorna resposta")
    void shouldThrowExceptionWhenResponseIsNull() {
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any(BodyInserters.FormInserter.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TokenResponseDTO.class)).thenReturn(Mono.empty());

        assertThrows(KeycloakTokenException.class, () -> tokenService.getAdminToken());
    }
}

