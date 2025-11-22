package com.backend.phi.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageAnalysisDto {

    @JsonProperty("incident_type")
    public String incidentType;

    public String severity;

    @JsonProperty("incident_description")
    public String incidentDescription;
}
