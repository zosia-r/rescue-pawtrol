package com.rescuepawtrol.backend.model;

import com.rescuepawtrol.backend.model.enums.AdoptionStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "adoption_card")
@Data
public class AdoptionCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AdoptionStatus status;
    private String candidateDetails;

    private LocalDate submissionDate;
    private LocalDate finalizationDate;

    @OneToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;
}