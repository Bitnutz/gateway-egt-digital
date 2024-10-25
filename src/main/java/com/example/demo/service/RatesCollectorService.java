package com.example.demo.service;

import com.example.demo.apigateway.RatesCollectorGateway;
import com.example.demo.dbmodel.AuditLog;
import com.example.demo.dbmodel.Rate;
import com.example.demo.dbmodel.RateHistorical;
import com.example.demo.model.*;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.RatesHistoricalRepository;
import com.example.demo.repository.RatesRepository;
import com.example.demo.xmlmodel.CurrentDataXml;
import com.example.demo.xmlmodel.PeriodDataXml;
import jakarta.annotation.PostConstruct;
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


    @Autowired
    private RatesHistoricalRepository ratesHistoricalRepository;
    @Autowired
    private RatesRepository ratesRepository;
    @Autowired
    private AuditLogRepository auditLogRepository;

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

        ratesHistoricalRepository.save(result);



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
        int deletedRates = ratesRepository.deleteOldRates();
        logger.info("Deleted {} old rates", deletedRates);
        currentRates.forEach(rate -> ratesRepository.save(rate));
        logger.info("Saved {} new rates into the database.", currentRates.size());
    }

    @Transactional
    public List<RateDTO> getJsonApiCurrentData(JsonApiCurrentRequest request) {
        saveApiRequest(request);
        logger.info("[JSON endpoint] Getting current fixer data for currency {}", request.getCurrency());
        return ratesRepository.getLatestRates(request.getCurrency())
                .stream()
                .map(rate ->
                        new RateDTO(rate.getTimestamp(), rate.getCurrencyName(), rate.getCurrencyValue())
                ).collect(Collectors.toList());
    }

    public List<RateDTO> getJsonApiHistoryData(JsonApiHistoryRequest request) {
        logger.info("[JSON endpoint] Getting history fixer data for currency {} and period {}", request.getCurrency(), request.getPeriod());
        ArrayList<Instant> historicalRateTimestamps = ratesHistoricalRepository.getLatestRateTimestamps(request.getPeriod());
        return ratesRepository.getRatesHistoryByTimestamp(request.getCurrency(), historicalRateTimestamps)
                .stream()
                .map(rate ->
                        new RateDTO(rate.getTimestamp(), rate.getCurrencyName(), rate.getCurrencyValue())
                ).collect(Collectors.toList());
    }

    public List<RateDTO> getXmlApiCurrentData(CurrentDataXml currentDataXml) {
        logger.info("[XML endpoint] Getting current fixer data for currency {}", currentDataXml.getGetRequest().getCurrency());
        //todo: save the request in the audit_log
        return ratesRepository.getLatestRates(currentDataXml.getGetRequest().getCurrency())
                .stream()
                .map(rate ->
                        new RateDTO(rate.getTimestamp(), rate.getCurrencyName(), rate.getCurrencyValue())
                ).collect(Collectors.toList());
    }

    public List<RateDTO> getXmlApiHistoryData(PeriodDataXml periodDataXml) {
        logger.info("[XML endpoint] Getting history fixer data for currency {} and period {}", periodDataXml.getHistoryRequest().getCurrency(), periodDataXml.getHistoryRequest().getPeriod());
        ArrayList<Instant> historicalRateTimestamps = ratesHistoricalRepository.getLatestRateTimestamps(periodDataXml.getHistoryRequest().getPeriod());
        return ratesRepository.getRatesHistoryByTimestamp(periodDataXml.getHistoryRequest().getCurrency(), historicalRateTimestamps)
                .stream()
                .map(rate ->
                        new RateDTO(rate.getTimestamp(), rate.getCurrencyName(), rate.getCurrencyValue())
                ).collect(Collectors.toList());
    }

    private void saveApiRequest(JsonApiCurrentRequest request) {
        if(auditLogRepository.findRequestId(request.getRequestId()) > 0){
            logger.error("The request ID {} is already present!", request.getRequestId());
            throw new DataIntegrityViolationException("Duplicate request ID for /json_api/current");
        }
        auditLogRepository.save(new AuditLog("JSON_API_CURRENT", request.getRequestId(), request.getTimestamp(), request.getClient()));
    }
    private void saveApiRequest(JsonApiHistoryRequest request) {

    }
    private void saveApiRequest(CurrentDataXml request) {

    }
    private void saveApiRequest(PeriodDataXml request) {

    }
}
