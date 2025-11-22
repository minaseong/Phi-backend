package com.backend.phi.chatbot.client;

import com.backend.phi.chatbot.dto.ChatRequestDto;
import com.backend.phi.chatbot.dto.ChatResponseDto;
import com.backend.phi.chatbot.dto.IncidentQueryNlpRequestDto;
import com.backend.phi.chatbot.dto.IncidentQueryNlpResponseDto;
import com.backend.phi.chatbot.dto.IncidentSummarizeNlpRequestDto;
import com.backend.phi.chatbot.dto.IncidentSummarizeNlpResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VlmClient {

    private final WebClient webClient;

    public VlmClient(@Value("${vlm.server.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ChatResponseDto sendChat(ChatRequestDto request) {
        return webClient.post()
                .uri("/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponseDto.class)
                .block();
    }

    public boolean isHealthy() {
        try {
            String result = webClient.get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return result != null && result.toLowerCase().contains("ok");
        } catch (Exception e) {
            return false;
        }
    }

    public IncidentQueryNlpResponseDto generateIncidentSql(IncidentQueryNlpRequestDto req) {
        return webClient.post()
                .uri("/incident_query")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(IncidentQueryNlpResponseDto.class)
                .block();
    }

    public IncidentSummarizeNlpResponseDto summarizeIncidents(IncidentSummarizeNlpRequestDto req) {
        return webClient.post()
                .uri("/incident_summarize")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(IncidentSummarizeNlpResponseDto.class)
                .block();
    }
}
