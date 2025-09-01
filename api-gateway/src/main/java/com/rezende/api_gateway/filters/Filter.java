package com.rezende.api_gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class Filter implements GlobalFilter {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .flatMap(c -> {
                    final Jwt jwt = (Jwt) c.getAuthentication().getPrincipal();
                    final String userId = jwt.getSubject();

                    final ServerWebExchange mutateExchange = exchange.mutate()
                            .request(r -> r.header("X-User-ID", userId))
                            .build();
                    return chain.filter(mutateExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
