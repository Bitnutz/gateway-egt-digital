package com.example.demo.controller;

import com.example.demo.service.RatesCollectorService;
import com.example.demo.xmlmodel.CurrentDataXml;
import com.example.demo.xmlmodel.PeriodDataXml;
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
    private RatesCollectorService ratesCollectorService;
    private final Logger logger = LoggerFactory.getLogger(XmlApi.class);


    private final String SEND_XML_REQUEST = "/xml_api/command";

    @PostMapping(value = SEND_XML_REQUEST, consumes = "application/xml", produces = "application/xml")
    public ResponseEntity<?> sendCurrentData(@RequestBody String request) throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        try {
            CurrentDataXml commandGet = xmlMapper.readValue(request, CurrentDataXml.class);
            return ResponseEntity.ok(ratesCollectorService.getXmlApiCurrentData(commandGet));
        } catch (JsonMappingException exception) {
            logger.error("No request for current data was spotted.\nProceeding with checking for history data.");
        }
        try {
            PeriodDataXml commandHistoryGet = xmlMapper.readValue(request, PeriodDataXml.class);
            return ResponseEntity.ok(ratesCollectorService.getXmlApiHistoryData(commandHistoryGet));
        } catch (JsonMappingException exception) {
            logger.error("No request for current data was spotted.\nProceeding with checking for history data.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The XML request has an invalid syntax: " + exception.getMessage());
        }
    }
}
