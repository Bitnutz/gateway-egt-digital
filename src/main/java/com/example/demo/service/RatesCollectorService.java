package com.example.demo.service;

import com.example.demo.apigateway.RatesCollectorGateway;
import com.example.demo.dbmodel.AuditLog;
import com.example.demo.dbmodel.Rate;
import com.example.demo.dbmodel.RateHistorical;
import com.example.demo.entityservice.AuditLogService;
import com.example.demo.entityservice.RateHistoricalService;
import com.example.demo.entityservice.RateService;
import com.example.demo.model.*;
import com.example.demo.xmlmodel.CurrentDataXml;
import com.example.demo.xmlmodel.PeriodDataXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatesCollectorService {
    private final RatesCollectorGateway ratesCollectorGateway;
    private final Logger logger = LoggerFactory.getLogger(RatesCollectorService.class);


//    @Autowired
//    private RatesHistoricalRepository ratesHistoricalRepository;
//    @Autowired
//    private RatesRepository ratesRepository;
//    @Autowired
//    private AuditLogRepository auditLogRepository;

    @Autowired
    private RateService rateService;

    @Autowired
    private RateHistoricalService rateHistoricalService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    public RatesCollectorService(RatesCollectorGateway ratesCollectorGateway) {
        this.ratesCollectorGateway = ratesCollectorGateway;
    }

    @Scheduled(cron = "${fetch.fixer-io.data.cron-job}")
    //@PostConstruct
    @Transactional
    public Boolean getFixerData() {
        FixerPayload payload = ratesCollectorGateway.fetchLatestFixerCurrencies();
        RateHistoricalDTO rateHistoricalDTO = new RateHistoricalDTO(payload.success(), payload.timestamp(), payload.base(), payload.date());
        RateHistorical result = new RateHistorical(rateHistoricalDTO.success(), rateHistoricalDTO.timestamp(), rateHistoricalDTO.base(), rateHistoricalDTO.date());

        rateHistoricalService.saveRateHistorical(result);

        List<Rate> ratesForTimestamp = payload.rates()
                .entrySet()
                .stream()
                .map(currency ->
                        new Rate(payload.timestamp(), currency.getKey(), currency.getValue()))
                .collect(Collectors.toList());

        updateCurrentRates(ratesForTimestamp);

        return true;
    }

    private void updateCurrentRates(List<Rate> currentRates) {
        rateService.deleteOldRates();
        currentRates.forEach(rate -> rateService.saveRate(rate));
    }

    @Transactional
    public List<RateDTO> getJsonApiCurrentData(JsonApiCurrentRequest request) {
        auditLogService.saveAuditLogWithLock(new AuditLog("JSON_API_CURRENT", request.getRequestId(), request.getTimestamp(), request.getClient()));
        logger.info("[JSON endpoint] Getting current fixer data for currency {}", request.getCurrency());
        return rateService.getLatestRates(request.getCurrency())
                .stream()
                .map(rate ->
                        new RateDTO(rate.getTimestamp(), rate.getCurrencyName(), rate.getCurrencyValue())
                ).collect(Collectors.toList());
    }

    @Transactional
    public List<Rate> getJsonApiHistoryData(JsonApiHistoryRequest request) {
        auditLogService.saveAuditLogWithLock(new AuditLog("JSON_API_HISTORY", request.getRequestId(), request.getTimestamp(), request.getClient()));
        logger.info("[JSON endpoint] Getting history fixer data for currency {} and period {}", request.getCurrency(), request.getPeriod());
        List<Instant> historicalRateTimestamps = rateHistoricalService.getLatestRateTimestampsByPeriod(request.getPeriod());
        return rateService.getRatesHistoryByTimestamp(request.getCurrency(), historicalRateTimestamps);
    }

    @Transactional
    public List<Rate> getXmlApiCurrentData(CurrentDataXml currentDataXml) {
        auditLogService.saveAuditLogWithLock(new AuditLog("XML_API_CURRENT", currentDataXml.getId(), Instant.now(), currentDataXml.getGetRequest().getConsumer()));
        logger.info("[XML endpoint] Getting current fixer data for currency {}", currentDataXml.getGetRequest().getCurrency());
        return rateService.getLatestRates(currentDataXml.getGetRequest().getCurrency());
    }

    @Transactional
    public List<Rate> getXmlApiHistoryData(PeriodDataXml periodDataXml) {
        auditLogService.saveAuditLogWithLock(new AuditLog("XML_API_PERIOD", periodDataXml.getId(), Instant.now(), periodDataXml.getHistoryRequest().getConsumer()));
        logger.info("[XML endpoint] Getting history fixer data for currency {} and period {}", periodDataXml.getHistoryRequest().getCurrency(), periodDataXml.getHistoryRequest().getPeriod());
        List<Instant>historicalRateTimestamps = rateHistoricalService.getLatestRateTimestampsByPeriod(periodDataXml.getHistoryRequest().getPeriod());
        return rateService.getRatesHistoryByTimestamp(periodDataXml.getHistoryRequest().getCurrency(), historicalRateTimestamps);
    }
}
