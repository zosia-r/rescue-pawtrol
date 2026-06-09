package com.rescuepawtrol.backend.repository;
import com.rescuepawtrol.backend.model.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    List<Intervention> findAllByReportTimeBetween(LocalDateTime start, LocalDateTime end);
}