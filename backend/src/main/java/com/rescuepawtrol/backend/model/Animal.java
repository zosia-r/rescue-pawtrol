package com.rescuepawtrol.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "animal")
@Data
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String species;
    private String name;
    private LocalDate birthDate;
    private String adoptionStatus;
    private Boolean isQuarantined;

    @ManyToOne
    @JoinColumn(name = "kennel_id")
    private Kennel kennel;

    @Transient
    public Integer getAge() {
        if (this.birthDate == null) {
            return 0;
        }
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }
}