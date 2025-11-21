package com.backend.phi.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Incident")
public class Incident {

    public enum IncidentType {
        CRIME, FIRE, DISASTER, ETC
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incident_id", updatable = false, nullable = false)
    private Long incidentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentType type;

    // Location stored as String for now (can be converted to Point later)
    // Format: "latitude,longitude" or WKT format "POINT(longitude latitude)"
    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false)
    private Integer urgency; // 0-255 (tinyint unsigned)

    @Column(nullable = false)
    private Integer credibility; // 0-255 (tinyint unsigned)

    @Column(name = "is_ongoing", nullable = false)
    private Boolean isOngoing = true;

    @Column(name = "first_reported_at", nullable = false, updatable = false)
    private Instant firstReportedAt;

    @Column(name = "last_reported_at")
    private Instant lastReportedAt;

    @Column(nullable = false, length = 1000)
    private String description;

    public Incident() {}

    @PrePersist
    private void prePersist() {
        if (firstReportedAt == null) {
            firstReportedAt = Instant.now();
        }
        if (lastReportedAt == null) {
            lastReportedAt = Instant.now();
        }
    }

    @PreUpdate
    private void preUpdate() {
        lastReportedAt = Instant.now();
    }

    // Getters and setters
    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }

    public IncidentType getType() { return type; }
    public void setType(IncidentType type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getUrgency() { return urgency; }
    public void setUrgency(Integer urgency) { this.urgency = urgency; }

    public Integer getCredibility() { return credibility; }
    public void setCredibility(Integer credibility) { this.credibility = credibility; }

    public Boolean getIsOngoing() { return isOngoing; }
    public void setIsOngoing(Boolean isOngoing) { this.isOngoing = isOngoing; }

    public Instant getFirstReportedAt() { return firstReportedAt; }
    public void setFirstReportedAt(Instant firstReportedAt) { this.firstReportedAt = firstReportedAt; }

    public Instant getLastReportedAt() { return lastReportedAt; }
    public void setLastReportedAt(Instant lastReportedAt) { this.lastReportedAt = lastReportedAt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

