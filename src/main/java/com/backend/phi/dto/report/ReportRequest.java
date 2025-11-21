package com.backend.phi.dto.report;

public class ReportRequest {

    private String location; // "latitude,longitude" format
    private String incidentClass;
    private Integer credibility;
    private Integer urgency;
    private String reportType; // "USER", "REDDIT", "NEWS"
    private Long incidentId; // If creating new report for existing incident
    private String description;
    private String incidentType; // For creating new incident: "CRIME", "FIRE", "DISASTER", "ETC"
    private String incidentDescription; // For creating new incident

    public ReportRequest() {}

    // Getters and setters
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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }

    public String getIncidentDescription() { return incidentDescription; }
    public void setIncidentDescription(String incidentDescription) { this.incidentDescription = incidentDescription; }
}

