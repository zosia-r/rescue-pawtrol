package com.rescuepawtrol.backend.model;

import com.rescuepawtrol.backend.model.enums.MedicalRecordType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "medical_record")
@Data
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate recordDate;

    @Enumerated(EnumType.STRING)
    private MedicalRecordType recordType;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String doctor;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;
}