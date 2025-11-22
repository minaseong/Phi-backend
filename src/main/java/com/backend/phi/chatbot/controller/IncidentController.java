package com.backend.phi.chatbot.controller;

import com.backend.phi.chatbot.client.VlmClient;
import com.backend.phi.chatbot.dto.*;
import com.backend.phi.chatbot.service.IncidentDbService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final VlmClient vlmClient;
    private final IncidentDbService incidentDbService;
    private final ObjectMapper objectMapper;

    public IncidentController(VlmClient vlmClient,
                              IncidentDbService incidentDbService,
                              ObjectMapper objectMapper) {
        this.vlmClient = vlmClient;
        this.incidentDbService = incidentDbService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/search", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String searchIncidents(@RequestBody String question) throws JsonProcessingException {

        IncidentQueryNlpRequestDto qReq = new IncidentQueryNlpRequestDto();
        qReq.question = question;
        IncidentQueryNlpResponseDto qResp = vlmClient.generateIncidentSql(qReq);
        String sql = qResp.sql;

        List<IncidentRecordDto> records = incidentDbService.runIncidentSelect(sql);
        String incidentsJson = objectMapper.writeValueAsString(records);

        IncidentSummarizeNlpRequestDto sReq = new IncidentSummarizeNlpRequestDto();
        sReq.question = question;
        sReq.incidents_json = incidentsJson;

        IncidentSummarizeNlpResponseDto sResp = vlmClient.summarizeIncidents(sReq);

        return sResp.reply;
    }
}
