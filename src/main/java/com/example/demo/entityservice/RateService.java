package com.example.demo.entityservice;

import com.example.demo.dbmodel.Rate;
import com.example.demo.model.RateDTO;
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
public class RateService {
    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(RateService.class);

    @Transactional
    public void saveRate(Rate rate) {
        entityManager.persist(rate);
        logger.info("Saved rate for {} currency.", rate.getCurrencyName());
    }

    @Transactional
    public List<Rate> getLatestRates(String currency) {
        String selectQuery = "SELECT r FROM Rate r WHERE r.currencyName = :currency AND r.deleteFlag = false";
        List<Rate> result = entityManager.createQuery(selectQuery, Rate.class)
                .setParameter("currency", currency)
                .getResultList();
        logger.info("Fetched the latest rates for currency {}", currency);
        return result;
    }

    @Transactional
    public void deleteOldRates() {
        String updateQuery = "UPDATE Rate SET deleteFlag = true";
        int deletedRows = entityManager.createQuery(updateQuery).executeUpdate();
        logger.info("Deleted {} old rates", deletedRows);
    }

    @Transactional
    public List<Rate> getRatesHistoryByTimestamp(String currency, List<Instant> timestamps) {
        String selectQuery = "SELECT r FROM Rate r WHERE r.currencyName = :currency AND r.timestamp in :timestamps";

        List<Rate> result = entityManager.createQuery(selectQuery, Rate.class)
                .setParameter("currency", currency)
                .setParameter("timestamps", timestamps)
                .getResultList();

        logger.info("Fetched {} rates for the currency {} for the following timestamps: {}", result.size(), currency, timestamps);
        return result;
    }

}
