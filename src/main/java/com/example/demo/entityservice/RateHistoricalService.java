package com.example.demo.entityservice;

import com.example.demo.dbmodel.RateHistorical;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RateHistoricalService {
    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(RateHistoricalService.class);

    @Transactional
    public void saveRateHistorical(RateHistorical rateHistorical) {
        entityManager.persist(rateHistorical);
        logger.info("Saved historical rate for base currency {}", rateHistorical.getBase());
    }

    @Transactional
    public List<Instant> getLatestRateTimestampsByPeriod(int period) {
        String selectQuery = "SELECT DISTINCT timestamp FROM Rate WHERE timestamp >= NOW() - make_interval(hours => :period)";

        List<Instant> result = entityManager.createQuery(selectQuery, Instant.class)
                .setParameter("period", period)
                .getResultList();
        logger.info("Fetched {} unique timestamps for period of {} hours.", result.size(), period);
        return result;
    }
}
