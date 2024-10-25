package com.example.demo.dbmodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.time.Instant;

@EnableAutoConfiguration
@Entity
@Table(name = "audit_log", schema = "gateway")
public class AuditLog{
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_log_sequence_generator")
        @SequenceGenerator(name = "audit_log_sequence_generator", sequenceName = "audit_log_sequence", schema = "gateway", allocationSize = 1)
        private Long id;
        private final String serviceId;
        private final String requestId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
        private final Instant time;
        private final String endClientId;

        public AuditLog(String serviceId, String requestId, Instant time, String endClientId) {
                this.serviceId = serviceId;
                this.requestId = requestId;
                this.time = time;
                this.endClientId = endClientId;
        }

        public String getServiceId() {
                return serviceId;
        }

        public String getRequestId() {
                return requestId;
        }

        public Instant getTime() {
                return time;
        }

        public String getEndClientId() {
                return endClientId;
        }
}
