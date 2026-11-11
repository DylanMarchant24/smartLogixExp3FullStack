package cl.duocuc.smartlogix.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(controllers = GatewaySecurityConfigTest.DummyGatewayController.class)
@Import({GatewaySecurityConfig.class, GatewaySecurityConfigTest.DummyGatewayController.class})
class GatewaySecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @RestController
    static class DummyGatewayController {
        @GetMapping("/api/bff/pedidos")
        public Map<String, String> listar() {
            return Map.of("status", "ok");
        }

        @PostMapping("/api/bff/pedidos")
        public Map<String, String> crear() {
            return Map.of("status", "created");
        }
    }

    @Test
    void getPublico_sinToken_retorna200() {
        webTestClient.get()
                .uri("/api/bff/pedidos")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void postProtegido_sinToken_retorna401Unauthorized() {
        webTestClient.post()
                .uri("/api/bff/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"skuProducto\":\"SKU-1\",\"cantidad\":1}")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Token JWT invalido o no proporcionado");
    }

    @Test
    void postProtegido_conJwtValido_retorna200() {
        webTestClient.mutateWith(mockJwt())
                .post()
                .uri("/api/bff/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"skuProducto\":\"SKU-1\",\"cantidad\":1}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("created");
    }
}
