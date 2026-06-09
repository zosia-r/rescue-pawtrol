package com.rescuepawtrol.backend.repository;

import com.rescuepawtrol.backend.model.AdoptionStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdoptionStatusHistoryRepository extends JpaRepository<AdoptionStatusHistory, Long> {
    List<AdoptionStatusHistory> findByAnimalIdOrderByDateDesc(Long animalId);
}
