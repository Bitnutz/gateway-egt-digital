package com.example.demo.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

public record FixerPayload(
        Boolean success,
        Instant timestamp,
        String base,
        LocalDate date,
        Map<String, Double> rates
) {
}
