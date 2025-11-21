package com.backend.phi.controller;

import com.backend.phi.dto.report.ReportRequest;
import com.backend.phi.dto.report.ReportResponse;
import com.backend.phi.entity.Report;
import com.backend.phi.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports/v1")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Helper: Convert Entity to Response DTO
    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getReportId(),
                report.getLocation(),
                report.getIncidentClass(),
                report.getCredibility(),
                report.getUrgency(),
                report.getReportType() != null ? report.getReportType().name() : null,
                report.getIncidentId(),
                report.getTimestamp(),
                report.getDescription()
        );
    }

    // Helper: Create Entity from Request DTO
    private Report fromRequest(ReportRequest request) {
        Report report = new Report();
        report.setLocation(request.getLocation());
        report.setIncidentClass(request.getIncidentClass());
        report.setCredibility(request.getCredibility() != null ? request.getCredibility() : 50);
        report.setUrgency(request.getUrgency() != null ? request.getUrgency() : 50);
        
        if (request.getReportType() != null) {
            report.setReportType(Report.ReportType.valueOf(request.getReportType().toUpperCase()));
        } else {
            report.setReportType(Report.ReportType.USER); // Default to USER
        }
        
        report.setDescription(request.getDescription());
        
        return report;
    }

    // ---------- CRUD endpoints ----------

    // GET /api/reports/v1 - Get all reports
    @GetMapping
    public List<ReportResponse> getAllReports() {
        return reportService.getAllReports()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /api/reports/v1/{id} - Get report by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .map(report -> ResponseEntity.ok(toResponse(report)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/reports/v1 - Create new report
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@RequestBody ReportRequest request) {
        try {
            Report report = fromRequest(request);
            Report saved;
            
            // If incidentId is provided, use it; otherwise create new incident
            if (request.getIncidentId() != null) {
                // Use existing incident
                report.setIncidentId(request.getIncidentId());
                saved = reportService.saveReport(report);
            } else {
                // Create new incident if needed
                if (request.getIncidentType() == null) {
                    return ResponseEntity.badRequest().build();
                }
                saved = reportService.createReportWithIncident(report, request);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // GET /api/reports/v1/incident/{incidentId} - Get all reports for an incident
    @GetMapping("/incident/{incidentId}")
    public List<ReportResponse> getReportsByIncidentId(@PathVariable Long incidentId) {
        return reportService.getReportsByIncidentId(incidentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // DELETE /api/reports/v1/{id} - Delete report
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        if (reportService.getReportById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reportService.deleteReportById(id);
        return ResponseEntity.noContent().build();
    }
}

