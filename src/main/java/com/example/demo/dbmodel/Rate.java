package com.example.demo.dbmodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.time.Instant;

@EnableAutoConfiguration
@Entity
@Table(name = "rate", schema = "gateway")
public class Rate {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rate_sequence_generator")
        @SequenceGenerator(name = "rate_sequence_generator", sequenceName = "rate_sequence", schema = "gateway", allocationSize = 1)
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
        private Instant timestamp;
        private String currencyName;
        private Double currencyValue;
        private final Boolean deleteFlag = false;

        public Rate(Instant timestamp, String currencyName, Double currencyValue) {
                this.timestamp = timestamp;
                this.currencyName = currencyName;
                this.currencyValue = currencyValue;
        }

        public Rate() {
        }

        public Instant getTimestamp() {
                return timestamp;
        }

        public String getCurrencyName() {
                return currencyName;
        }

        public Double getCurrencyValue() {
                return currencyValue;
        }
}
