package com.backend.phi.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ChatRequestDto {

    public List<HistoryMessageDto> history;

    @JsonProperty("user_message")
    public String userMessage;

    @JsonProperty("image_base64")
    public String imageBase64;

    @JsonProperty("max_new_tokens")
    public Integer maxNewTokens = 256;
}
