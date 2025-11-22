package com.backend.phi.chatbot.session;

import com.backend.phi.chatbot.dto.HistoryMessageDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatSession {

    private List<HistoryMessageDto> history = new ArrayList<>();
    private Instant lastActivity = Instant.now();

    public List<HistoryMessageDto> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryMessageDto> history) {
        this.history = history;
    }

    public Instant getLastActivity() {
        return lastActivity;
    }

    public void touch() {
        this.lastActivity = Instant.now();
    }
}
