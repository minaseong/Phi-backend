package com.backend.phi.service;

import com.backend.phi.dto.report.ReportRequest;
import com.backend.phi.entity.Report;
import com.backend.phi.entity.Incident;
import com.backend.phi.repository.ReportRepository;
import com.backend.phi.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final IncidentRepository incidentRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository, IncidentRepository incidentRepository) {
        this.reportRepository = reportRepository;
        this.incidentRepository = incidentRepository;
    }

    // Create or update report
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    // Get all reports
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Get report by ID
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    // Get reports by incident ID
    public List<Report> getReportsByIncidentId(Long incidentId) {
        return reportRepository.findByIncidentId(incidentId);
    }

    // Get reports by type
    public List<Report> getReportsByType(Report.ReportType reportType) {
        return reportRepository.findByReportType(reportType);
    }

    // Delete report by ID
    public void deleteReportById(Long id) {
        reportRepository.deleteById(id);
    }

    // Create incident
    public Incident saveIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    // Get incident by ID
    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    // Create report with incident creation if needed
    @Transactional
    public Report createReportWithIncident(Report report, ReportRequest request) {
        // If incidentId is provided, use existing incident
        // Otherwise, create new incident
        Long incidentId;
        
        if (request.getIncidentId() != null) {
            // Use existing incident
            incidentId = request.getIncidentId();
            // Update incident's last_reported_at
            Optional<Incident> existingIncident = incidentRepository.findById(incidentId);
            if (existingIncident.isPresent()) {
                Incident incident = existingIncident.get();
                incident.setLastReportedAt(java.time.Instant.now());
                incidentRepository.save(incident);
            }
        } else {
            // Create new incident
            Incident newIncident = new Incident();
            newIncident.setLocation(request.getLocation());
            newIncident.setType(Incident.IncidentType.valueOf(request.getIncidentType().toUpperCase()));
            newIncident.setUrgency(request.getUrgency());
            newIncident.setCredibility(request.getCredibility());
            newIncident.setDescription(request.getIncidentDescription() != null ? 
                request.getIncidentDescription() : request.getDescription());
            newIncident.setIsOngoing(true);
            
            Incident savedIncident = incidentRepository.save(newIncident);
            incidentId = savedIncident.getIncidentId();
        }
        
        report.setIncidentId(incidentId);
        return reportRepository.save(report);
    }
}

