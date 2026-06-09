package com.rescuepawtrol.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "adoption_status_history")
@Data
public class AdoptionStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String status;
    private String potentialOwner;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;
}
