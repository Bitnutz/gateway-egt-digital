package com.example.demo.controller;

import com.example.demo.model.XmlApiCurrentRequest;
import com.example.demo.model.XmlApiPeriodRequest;
import com.example.demo.service.StatisticsCollectorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class XmlApi {
    @Autowired
    private StatisticsCollectorService statisticsCollectorService;

    private final Logger logger = LoggerFactory.getLogger(XmlApi.class);

    private final String SEND_XML_REQUEST = "/xml_api/command";

    @PostMapping(value = SEND_XML_REQUEST, consumes = "application/xml", produces = "application/xml")
    public ResponseEntity<?> sendCurrentData(@RequestBody String request) throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            XmlApiCurrentRequest commandGet = xmlMapper.readValue(request, XmlApiCurrentRequest.class);
            return ResponseEntity.ok(statisticsCollectorService.getXmlApiCurrentData(commandGet));
        } catch (JsonMappingException exception) {
            logger.error("[XML Controller] No request for current data was spotted. Proceeding with checking for history data.");
        } catch (IllegalStateException duplicateException) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(duplicateException.getMessage());
        }
        try {
            XmlApiPeriodRequest commandHistoryGet = xmlMapper.readValue(request, XmlApiPeriodRequest.class);
            return ResponseEntity.ok(statisticsCollectorService.getXmlApiHistoryData(commandHistoryGet));
        } catch (JsonMappingException exception) {
            logger.error("[XML Controller] No request for history data was spotted. The XML request has an invalid syntax");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The XML request has an invalid syntax: " + exception.getMessage());
        } catch (IllegalStateException duplicateException) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(duplicateException.getMessage());
        }
    }
}
