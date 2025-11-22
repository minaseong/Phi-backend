package com.backend.phi.chatbot.session;

import com.backend.phi.chatbot.dto.HistoryMessageDto;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionStore {

    private static final Duration TIMEOUT = Duration.ofMinutes(10);

    private final ConcurrentHashMap<String, ChatSession> sessions = new ConcurrentHashMap<>();

    private ChatSession getOrCreateSession(String sessionId) {
        cleanup();
        return sessions.computeIfAbsent(sessionId, id -> new ChatSession());
    }

    private void cleanup() {
        Instant now = Instant.now();
        sessions.entrySet().removeIf(entry -> {
            ChatSession s = entry.getValue();
            return Duration.between(s.getLastActivity(), now).compareTo(TIMEOUT) > 0;
        });
    }

    public List<HistoryMessageDto> getHistory(String sessionId) {
        return new ArrayList<>(getOrCreateSession(sessionId).getHistory());
    }

    public void appendMessage(String sessionId, String role, String content) {
        ChatSession session = getOrCreateSession(sessionId);
        HistoryMessageDto msg = new HistoryMessageDto();
        msg.role = role;
        msg.content = content;
        session.getHistory().add(msg);
        session.touch();
    }
}
