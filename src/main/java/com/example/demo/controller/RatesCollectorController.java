package com.example.demo.controller;


import com.example.demo.service.RatesCollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class RatesCollectorController {
    @Autowired
    private final RatesCollectorService ratesCollectorService;
    private final Logger logger = LoggerFactory.getLogger(RatesCollectorController.class);
    public RatesCollectorController(RatesCollectorService ratesCollectorService) {
        this.ratesCollectorService = ratesCollectorService;
    }

    // might delete the whole controller
    @GetMapping("/fetch-fixer-data")
    public String getFixerIOData() {
        ratesCollectorService.getFixerData();
        logger.info("Fetched fixer.io currencies.");
        return "Fetched fixer.io currencies.";
    }

}
