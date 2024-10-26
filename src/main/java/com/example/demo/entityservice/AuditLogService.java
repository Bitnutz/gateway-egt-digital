package com.example.demo.entityservice;

import com.example.demo.dbmodel.AuditLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {
    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    @Transactional
    public void saveAuditLogWithLock(AuditLog auditLog) {
        String selectQuery = "SELECT 1 FROM AuditLog WHERE requestId = :request_id";
        int hasDuplicate = entityManager.createQuery(selectQuery)
                .setParameter("request_id", auditLog.getRequestId())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList().size();

        if(hasDuplicate > 0) {
            logger.error("The request ID {} is already present!", auditLog.getRequestId());
            throw new IllegalStateException("Customer with this email already exists");
        }
        entityManager.merge(auditLog);
        logger.info("Saved auditLog record with request ID: {}", auditLog.getRequestId());
    }
}
