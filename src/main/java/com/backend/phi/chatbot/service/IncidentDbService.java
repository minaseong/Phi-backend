package com.backend.phi.chatbot.service;

import com.backend.phi.chatbot.dto.IncidentRecordDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
public class IncidentDbService {

    private final JdbcTemplate jdbcTemplate;

    public IncidentDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // LLM이 생성한 SQL 질의문 실행
    public List<IncidentRecordDto> runIncidentSelect(String sql) {
        String lower = sql.toLowerCase().trim();
        if (!lower.startsWith("select") || !lower.contains("incident")) {
            System.err.println("[IncidentDbService] Unsafe or invalid SQL: " + sql);
            return Collections.emptyList();
        }

        try {
            return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> mapRow(rs));
        } catch (Exception e) {
            System.err.println("[IncidentDbService] Error running SQL: " + sql);
            e.printStackTrace();
            // 에러 나도 챗봇이 죽지 않게, 빈 리스트 반환
            return Collections.emptyList();
        }
    }

    private IncidentRecordDto mapRow(ResultSet rs) throws SQLException {
        IncidentRecordDto dto = new IncidentRecordDto();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        for (int i = 1; i <= cols; i++) {
            String colName = md.getColumnLabel(i);
            Object value = rs.getObject(i);
            dto.put(colName, value);
        }

        return dto;
    }
}
