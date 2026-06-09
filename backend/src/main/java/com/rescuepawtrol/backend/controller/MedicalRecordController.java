package com.rescuepawtrol.backend.controller;

import com.rescuepawtrol.backend.model.MedicalRecord;
import com.rescuepawtrol.backend.model.enums.MedicalRecordType;
import com.rescuepawtrol.backend.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping
    public List<MedicalRecord> getAllRecords() {
        return medicalRecordService.getAllRecords();
    }

    @GetMapping("/types")
    public List<MedicalRecordType> getMedicalRecordTypes() {
        return Arrays.asList(MedicalRecordType.values());
    }

    @GetMapping("/animal/{animalId}")
    public List<MedicalRecord> getRecordsByAnimal(@PathVariable Long animalId) {
        return medicalRecordService.getRecordsByAnimalId(animalId);
    }

    @PostMapping
    public MedicalRecord addRecord(@RequestBody MedicalRecord record) {
        return medicalRecordService.saveRecord(record);
    }
}