package com.example.demo.apigateway;

import com.example.demo.model.FixerPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RatesCollectorGateway {
    @Value("${fixer-io.api.key}")
    private String apiKey;

    @Autowired
    private final RestTemplate restTemplate;

    public RatesCollectorGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public FixerPayload fetchLatestFixerCurrencies() {
        ResponseEntity<FixerPayload> resp =restTemplate.getForEntity("https://data.fixer.io/api/latest?access_key=" + apiKey, FixerPayload.class);
        return resp.getBody();
    }
}
