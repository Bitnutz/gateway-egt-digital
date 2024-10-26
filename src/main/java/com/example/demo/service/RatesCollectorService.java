package com.example.demo.service;

import com.example.demo.apigateway.RatesCollectorGateway;
import com.example.demo.dbmodel.Rate;
import com.example.demo.dbmodel.RateHistorical;
import com.example.demo.entityservice.AuditLogService;
import com.example.demo.entityservice.RateHistoricalService;
import com.example.demo.entityservice.RateService;
import com.example.demo.model.FixerPayload;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatesCollectorService {
    private final Logger logger = LoggerFactory.getLogger(RatesCollectorService.class);

    @Autowired
    private RatesCollectorGateway ratesCollectorGateway;

    @Autowired
    private RateService rateService;

    @Autowired
    private RateHistoricalService rateHistoricalService;

    @Scheduled(cron = "${fetch.fixer-io.data.cron-job}")
    @PostConstruct
    @Transactional
    public void getFixerData() {
        FixerPayload payload = ratesCollectorGateway.fetchLatestFixerCurrencies();
        RateHistorical rateHistorical = new RateHistorical(payload.success(), payload.timestamp(), payload.base(), payload.date());
        rateHistoricalService.saveRateHistorical(rateHistorical);

        List<Rate> ratesForTimestamp = payload.rates()
                .entrySet()
                .stream()
                .map(currency ->
                        new Rate(payload.timestamp(), currency.getKey(), currency.getValue()))
                .collect(Collectors.toList());

        updateCurrentRates(ratesForTimestamp);
    }

    private void updateCurrentRates(List<Rate> currentRates) {
        rateService.deleteOldRates();
        currentRates.forEach(rate -> rateService.saveRate(rate));
    }
}
