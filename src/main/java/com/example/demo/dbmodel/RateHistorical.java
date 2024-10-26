package com.example.demo.dbmodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.time.Instant;
import java.time.LocalDate;

@EnableAutoConfiguration
@Entity
@Table(name = "rate_historical", schema = "gateway")
public class RateHistorical {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rate_historical_sequence_generator")
        @SequenceGenerator(name = "rate_historical_sequence_generator", sequenceName = "rate_sequence",schema = "gateway", allocationSize = 1)
        private Long id;
        private Boolean success;
        @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
        @Column(nullable = false, unique = true)
        private Instant timestamp;
        private String base;
        private LocalDate date;
        private Boolean deleteFlag;

        public RateHistorical() {}

        public RateHistorical(Boolean success, Instant timestamp, String base, LocalDate date) {
                this.success = success;
                this.timestamp = timestamp;
                this.base = base;
                this.date = date;
        }

        public Boolean getSuccess() {
                return success;
        }

        public Instant getTimestamp() {
                return timestamp;
        }

        public String getBase() {
                return base;
        }

        public LocalDate getDate() {
                return date;
        }
}
