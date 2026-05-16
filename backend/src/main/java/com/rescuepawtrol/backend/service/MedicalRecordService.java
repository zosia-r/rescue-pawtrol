package com.rescuepawtrol.backend.service;

import com.rescuepawtrol.backend.model.MedicalRecord;
import com.rescuepawtrol.backend.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;

    public List<MedicalRecord> getAllRecords() {
        return medicalRecordRepository.findAll();
    }

    public List<MedicalRecord> getRecordsByAnimalId(Long animalId) {
        return medicalRecordRepository.findByAnimalId(animalId);
    }

    public MedicalRecord saveRecord(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }
}