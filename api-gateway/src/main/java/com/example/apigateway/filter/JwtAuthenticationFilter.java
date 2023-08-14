package com.example.apigateway.filter;

import com.example.apigateway.constant.FilterConstant;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private String secretKey = FilterConstant.SECRET_KEY;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader;

            if (request.getURI().getPath().contains(FilterConstant.ADMIN_ROLE)) {
                if (!Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody().get("role", String.class).equals(FilterConstant.ADMIN_ROLE)) {
                    return onError(exchange, "Not have access", HttpStatus.UNAUTHORIZED);
                }
            }

            if (request.getURI().getPath().contains("invoice/add")) {
                if (!Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody().get("status", String.class).equals(FilterConstant.USER_STATUS_VERIFIED)) {
                    return onError(exchange, "Unverified email", HttpStatus.UNAUTHORIZED);
                }
            }

            if (isNotJwtValid(jwt)) {
                return onError(exchange, "JWT token is expired", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private boolean isNotJwtValid(String jwt) {
        return Optional.ofNullable(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody().getExpiration())
                .map(expiration -> expiration.before(new Date()))
                .orElse(true);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

    public static class Config {
    }
}
