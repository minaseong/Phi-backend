package com.backend.phi.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IncidentSummarizeNlpRequestDto {

    public String question;

    @JsonProperty("incidents_json")
    public String incidents_json;
}
