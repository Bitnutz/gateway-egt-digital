package com.example.demo.service;

import com.example.demo.dbmodel.AuditLog;
import com.example.demo.dbmodel.Rate;
import com.example.demo.entityservice.AuditLogService;
import com.example.demo.entityservice.RateHistoricalService;
import com.example.demo.entityservice.RateService;
import com.example.demo.model.XmlApiCurrentRequest;
import com.example.demo.model.JsonApiCurrentRequest;
import com.example.demo.model.JsonApiHistoryRequest;
import com.example.demo.model.XmlApiPeriodRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class StatisticsCollectorService {
    private final Logger logger = LoggerFactory.getLogger(StatisticsCollectorService.class);

    @Autowired
    private RateService rateService;

    @Autowired
    private RateHistoricalService rateHistoricalService;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public List<Rate> getJsonApiCurrentData(JsonApiCurrentRequest request) {
        auditLogService.saveAuditLogWithLock(new AuditLog("JSON_API_CURRENT", request.getRequestId(), request.getTimestamp(), request.getClient()));
        logger.info("[JSON endpoint] Getting current fixer data for currency {}", request.getCurrency());
        return rateService.getLatestRates(request.getCurrency());
    }

    @Transactional
    public List<Rate> getJsonApiHistoryData(JsonApiHistoryRequest request) {
        auditLogService.saveAuditLogWithLock(new AuditLog("JSON_API_HISTORY", request.getRequestId(), request.getTimestamp(), request.getClient()));
        logger.info("[JSON endpoint] Getting history fixer data for currency {} and period {}", request.getCurrency(), request.getPeriod());
        List<Instant> historicalRateTimestamps = rateHistoricalService.getLatestRateTimestampsByPeriod(request.getPeriod());
        return rateService.getRatesHistoryByTimestamp(request.getCurrency(), historicalRateTimestamps);
    }

    @Transactional
    public List<Rate> getXmlApiCurrentData(XmlApiCurrentRequest xmlApiCurrentRequest) {
        auditLogService.saveAuditLogWithLock(new AuditLog("XML_API_CURRENT", xmlApiCurrentRequest.getId(), Instant.now(), xmlApiCurrentRequest.getGetRequest().getConsumer()));
        logger.info("[XML endpoint] Getting current fixer data for currency {}", xmlApiCurrentRequest.getGetRequest().getCurrency());
        return rateService.getLatestRates(xmlApiCurrentRequest.getGetRequest().getCurrency());
    }

    @Transactional
    public List<Rate> getXmlApiHistoryData(XmlApiPeriodRequest xmlApiPeriodRequest) {
        auditLogService.saveAuditLogWithLock(new AuditLog("XML_API_PERIOD", xmlApiPeriodRequest.getId(), Instant.now(), xmlApiPeriodRequest.getHistoryRequest().getConsumer()));
        logger.info("[XML endpoint] Getting history fixer data for currency {} and period {}", xmlApiPeriodRequest.getHistoryRequest().getCurrency(), xmlApiPeriodRequest.getHistoryRequest().getPeriod());
        List<Instant> historicalRateTimestamps = rateHistoricalService.getLatestRateTimestampsByPeriod(xmlApiPeriodRequest.getHistoryRequest().getPeriod());
        return rateService.getRatesHistoryByTimestamp(xmlApiPeriodRequest.getHistoryRequest().getCurrency(), historicalRateTimestamps);
    }
}
