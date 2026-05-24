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

    @Query("SELECT FUNCTION('date', i.reportTime), COUNT(i) FROM Intervention i " +
            "WHERE i.reportTime >= :start AND i.reportTime <= :end " +
            "GROUP BY FUNCTION('date', i.reportTime) ORDER BY FUNCTION('date', i.reportTime) ASC")
    List<Object[]> countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}