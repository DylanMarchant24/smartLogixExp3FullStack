package cl.duocuc.smartlogix.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Configuración de Seguridad OAuth 2.0 / OpenID Connect (Resource Server) en el API Gateway.
 * Valida la firma, expiración (exp) e issuer-uri de los tokens JWT emitidos por Azure Entra ID (IDaaS).
 * Rechaza con 401 Unauthorized peticiones mutadoras sin token o con token inválido/expirado.
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://127.0.0.1:3000,http://localhost:4200,http://127.0.0.1:4200,*}")
    private String allowedOrigins;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            byte[] bytes = "{\"error\":\"Token JWT invalido o no proporcionado\"}"
                                    .getBytes(StandardCharsets.UTF_8);
                            DataBuffer buffer = response.bufferFactory().wrap(bytes);
                            return response.writeWith(Mono.just(buffer));
                        })
                        .accessDeniedHandler((exchange, ex) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            byte[] bytes = "{\"error\":\"No tienes permisos para acceder a este recurso\"}"
                                    .getBytes(StandardCharsets.UTF_8);
                            DataBuffer buffer = response.bufferFactory().wrap(bytes);
                            return response.writeWith(Mono.just(buffer));
                        })
                )
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/bff/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/**").permitAll()

                        // Endpoints de escritura/mutación de negocio protegidos con JWT OAuth2 (IDaaS)
                        .pathMatchers(HttpMethod.POST, "/api/bff/**").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/api/bff/**").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/bff/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/bff/**").authenticated()

                        .pathMatchers(HttpMethod.POST, "/api/inventario/**").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/api/inventario/**").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/inventario/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/inventario/**").authenticated()

                        .pathMatchers(HttpMethod.POST, "/api/pedidos/**").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/api/pedidos/**").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/pedidos/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/pedidos/**").authenticated()

                        .pathMatchers(HttpMethod.POST, "/api/envios/**").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/api/envios/**").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/envios/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/envios/**").authenticated()

                        .anyExchange().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        config.setAllowedOriginPatterns(origins.isEmpty() ? List.of("*") : origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

