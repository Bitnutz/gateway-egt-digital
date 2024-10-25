package com.example.demo.model;

import java.time.Instant;
import java.time.LocalDate;

public record RateHistoricalDTO(Boolean success, Instant timestamp, String base, LocalDate date) {
}
