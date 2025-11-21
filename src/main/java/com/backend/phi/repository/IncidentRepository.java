package com.backend.phi.repository;

import com.backend.phi.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByType(Incident.IncidentType type);
    List<Incident> findByIsOngoing(Boolean isOngoing);
}

