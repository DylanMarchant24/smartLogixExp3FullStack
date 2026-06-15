package ${package}.service;

import ${package}.dto.SummaryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class BffService {
    private final RestTemplate restTemplate;

    @Value("${service.a.url:http://localhost:8081}")
    private String serviceAUrl;

    @Value("${service.b.url:http://localhost:8082}")
    private String serviceBUrl;

    public BffService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SummaryDTO getSummary() {
        return new SummaryDTO(
                fetchList(serviceAUrl + "/api/items"),
                fetchList(serviceBUrl + "/api/items")
        );
    }

    private List<Map<String, Object>> fetchList(String url) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            return Collections.emptyList();
        }
    }
}
