package com.example.demo.repository;

import com.example.demo.dbmodel.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(value = "SELECT COUNT(*) FROM gateway.audit_log where request_id = :requestId", nativeQuery = true)
    Integer findRequestId(@Param("requestId")String requestId);
}
