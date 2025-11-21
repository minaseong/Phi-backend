package com.backend.phi.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Report")
public class Report {

    public enum ReportType {
        USER, REDDIT, NEWS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", updatable = false, nullable = false)
    private Long reportId;

    // Location stored as String for now (can be converted to Point later)
    // Format: "latitude,longitude" or WKT format "POINT(longitude latitude)"
    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "incident_class", nullable = false, length = 50)
    private String incidentClass;

    @Column(nullable = false)
    private Integer credibility; // 0-255 (tinyint unsigned)

    @Column(nullable = false)
    private Integer urgency; // 0-255 (tinyint unsigned)

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Column(name = "incident_id", nullable = false)
    private Long incidentId; // Foreign key to Incident table

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Report() {}

    @PrePersist
    private void prePersist() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
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

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

