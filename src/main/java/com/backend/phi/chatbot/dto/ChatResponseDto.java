package com.backend.phi.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatResponseDto {

    public String reply;

    @JsonProperty("image_analysis")
    public ImageAnalysisDto imageAnalysis;

    public String intent;

    @JsonProperty("show_report_button")
    public Boolean showReportButton;

    @JsonProperty("report_button_label")
    public String reportButtonLabel;
}
