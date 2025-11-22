package com.backend.phi.chatbot.controller;

import com.backend.phi.chatbot.client.VlmClient;
import com.backend.phi.chatbot.dto.*;
import com.backend.phi.chatbot.service.IncidentDbService;
import com.backend.phi.chatbot.session.ChatSessionStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final VlmClient vlmClient;
    private final ChatSessionStore sessionStore;
    private final IncidentDbService incidentDbService;
    private final ObjectMapper objectMapper;

    public ChatController(VlmClient vlmClient,
                          ChatSessionStore sessionStore,
                          IncidentDbService incidentDbService,
                          ObjectMapper objectMapper) {
        this.vlmClient = vlmClient;
        this.sessionStore = sessionStore;
        this.incidentDbService = incidentDbService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ChatResponseDto chat(
            @RequestParam("sessionId") String sessionId,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException, JsonProcessingException {

        String userMessage = (text == null) ? "" : text.trim();

        if (userMessage.isEmpty() && (image == null || image.isEmpty())) {
            ChatResponseDto resp = new ChatResponseDto();
            resp.reply = "Please enter a message or attach an image.";
            return resp;
        }

        // 최근 대화 히스토리 가져오기
        List<HistoryMessageDto> history = sessionStore.getHistory(sessionId);

        // VLM /chat 요청 DTO 구성
        ChatRequestDto req = new ChatRequestDto();
        req.history = history;
        req.userMessage = userMessage.isEmpty()
                ? "Please analyze this image."
                : userMessage;
        req.maxNewTokens = 256;

        if (image != null && !image.isEmpty()) {
            byte[] bytes = image.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            req.imageBase64 = base64;
        } else {
            req.imageBase64 = null;
        }

        // 유저 메시지 히스토리에 추가
        if (!userMessage.isEmpty()) {
            sessionStore.appendMessage(sessionId, "user", userMessage);
        } else if (image != null && !image.isEmpty()) {
            sessionStore.appendMessage(sessionId, "user", "[Image uploaded]");
        }

        // VLM /chat 호출
        ChatResponseDto resp = vlmClient.sendChat(req);

        // intent 가 incident_query 라면, DB 조회 + 요약 플로우 수행
        if (resp != null && "incident_query".equals(resp.intent)) {
            System.out.println("[ChatController] Detected intent=incident_query for message: " + userMessage);
            try {
                IncidentQueryNlpRequestDto qReq = new IncidentQueryNlpRequestDto();
                qReq.question = userMessage;

                IncidentQueryNlpResponseDto qResp = vlmClient.generateIncidentSql(qReq);
                String sql = qResp.sql;
                System.out.println("[ChatController] Generated SQL: " + sql);

                List<IncidentRecordDto> records = incidentDbService.runIncidentSelect(sql);
                String incidentsJson = objectMapper.writeValueAsString(records);
                System.out.println("[ChatController] incidentsJson: " + incidentsJson);

                IncidentSummarizeNlpRequestDto sReq = new IncidentSummarizeNlpRequestDto();
                sReq.question = userMessage;
                sReq.incidents_json = incidentsJson;

                IncidentSummarizeNlpResponseDto sResp = vlmClient.summarizeIncidents(sReq);

                if (resp == null) {
                    resp = new ChatResponseDto();
                }
                resp.reply = sResp.reply;
            } catch (Exception e) {
                System.err.println("[ChatController] Error during incident_query flow");
                e.printStackTrace();
                if (resp == null) {
                    resp = new ChatResponseDto();
                }
                resp.reply = "Sorry, I couldn't complete the incident search due to an internal error.";
            }
        }

        // 어시스턴트 최종 답변을 히스토리에 추가
        if (resp != null && resp.reply != null) {
            sessionStore.appendMessage(sessionId, "assistant", resp.reply);
        }

        // 이미지 분석 결과가 있으면 텍스트로도 히스토리에 저장
        if (resp != null && resp.imageAnalysis != null) {
            ImageAnalysisDto ia = resp.imageAnalysis;
            String a = ia.incidentType;
            String s = ia.severity;
            String d = ia.incidentDescription;
            String analysisText = String.format(
                    "[IMAGE_ANALYSIS] type=%s, severity=%s, desc=%s",
                    a, s, d
            );
            sessionStore.appendMessage(sessionId, "assistant", analysisText);
        }

        return resp;
    }

    @GetMapping("/health")
    public String health() {
        boolean ok = vlmClient.isHealthy();
        return ok ? "VLM server OK" : "VLM server NOT reachable";
    }
}
