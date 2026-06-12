package com.rescuepawtrol.backend.repository;
import com.rescuepawtrol.backend.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByAnimalId(Long animalId);

    List<MedicalRecord> findAllByRecordDateBetween(LocalDate start, LocalDate end);
}
