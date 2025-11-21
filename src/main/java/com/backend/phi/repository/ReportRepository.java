package com.backend.phi.repository;

import com.backend.phi.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByIncidentId(Long incidentId);
    List<Report> findByReportType(Report.ReportType reportType);
}

