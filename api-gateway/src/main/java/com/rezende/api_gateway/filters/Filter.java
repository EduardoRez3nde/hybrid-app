package com.rezende.api_gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class Filter implements GlobalFilter {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .flatMap(c -> {
                    final Jwt jwt = (Jwt) c.getAuthentication().getPrincipal();
                    final String userId = jwt.getSubject();

                    log.debug("JWT encontrado. subject(userId)={}, claims={}", userId, jwt.getClaims());

                    final ServerWebExchange mutateExchange = exchange.mutate()
                            .request(r -> {
                                log.debug("Adicionando cabeçalho X-User-ID={} à requisição {}", userId, exchange.getRequest().getURI());
                                r.header("X-User-ID", userId);
                            })
                            .build();

                    return chain.filter(mutateExchange);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Nenhuma autenticação encontrada no contexto. Requisição seguirá sem X-User-ID. URI={}", exchange.getRequest().getURI());
                    return chain.filter(exchange);
                }));
    }
}
