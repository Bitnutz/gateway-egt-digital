package com.example.demo.controller;

import com.example.demo.model.JsonApiCurrentRequest;
import com.example.demo.model.JsonApiHistoryRequest;
import com.example.demo.service.RatesCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class JsonApi {
    @Autowired
    private RatesCollectorService ratesCollectorService;

    private final String SEND_CURRENT = "/json_api/current";
    private final String SEND_HISTORY = "/json_api/history";


    @PostMapping(SEND_CURRENT)
    public ResponseEntity<?> sendCurrentData(@RequestBody JsonApiCurrentRequest request) {
        return ResponseEntity.ok(ratesCollectorService.getJsonApiCurrentData(request));
    }

    @PostMapping(SEND_HISTORY)
    public ResponseEntity<?> sendHistoryData(@RequestBody JsonApiHistoryRequest request) {
        return ResponseEntity.ok(ratesCollectorService.getJsonApiHistoryData(request));
    }
}
