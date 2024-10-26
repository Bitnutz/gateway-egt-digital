package com.example.demo.controller;

import com.example.demo.model.JsonApiCurrentRequest;
import com.example.demo.model.JsonApiHistoryRequest;
import com.example.demo.service.StatisticsCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class JsonApi {
    private final String SEND_CURRENT = "/json_api/current";
    private final String SEND_HISTORY = "/json_api/history";

    @Autowired
    private StatisticsCollectorService statisticsCollectorService;

    @PostMapping(SEND_CURRENT)
    public ResponseEntity<?> sendCurrentData(@RequestBody JsonApiCurrentRequest request) {
        try {
            return ResponseEntity.ok(statisticsCollectorService.getJsonApiCurrentData(request));
        } catch (IllegalStateException exception) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(exception.getMessage());
        }
    }

    @PostMapping(SEND_HISTORY)
    public ResponseEntity<?> sendHistoryData(@RequestBody JsonApiHistoryRequest request) {
        try {
            return ResponseEntity.ok(statisticsCollectorService.getJsonApiHistoryData(request));
        } catch (IllegalStateException exception) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(exception.getMessage());
        }
    }
}
