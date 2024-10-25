package com.example.demo.model;

import java.time.Instant;

public record RateDTO(
        Instant timestamp,
        String currencyName,
        Double currencyValue
) {
}
