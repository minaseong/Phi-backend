package com.backend.phi.dto.report;

import java.time.Instant;

public class ReportResponse {

    private Long reportId;
    private String location;
    private String incidentClass;
    private Integer credibility;
    private Integer urgency;
    private String reportType;
    private Long incidentId;
    private Instant timestamp;
    private String description;

    public ReportResponse() {}

    public ReportResponse(Long reportId,
                         String location,
                         String incidentClass,
                         Integer credibility,
                         Integer urgency,
                         String reportType,
                         Long incidentId,
                         Instant timestamp,
                         String description) {
        this.reportId = reportId;
        this.location = location;
        this.incidentClass = incidentClass;
        this.credibility = credibility;
        this.urgency = urgency;
        this.reportType = reportType;
        this.incidentId = incidentId;
        this.timestamp = timestamp;
        this.description = description;
    }

    // Getters and setters
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getIncidentClass() { return incidentClass; }
    public void setIncidentClass(String incidentClass) { this.incidentClass = incidentClass; }

    public Integer getCredibility() { return credibility; }
    public void setCredibility(Integer credibility) { this.credibility = credibility; }

    public Integer getUrgency() { return urgency; }
    public void setUrgency(Integer urgency) { this.urgency = urgency; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

