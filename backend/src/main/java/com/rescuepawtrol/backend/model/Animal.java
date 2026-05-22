package com.rescuepawtrol.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "animal")
@Data
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String species;
    private String name;
    private Integer age;
    private String adoptionStatus;
    private Boolean isQuarantined;

    @ManyToOne
    @JoinColumn(name = "kennel_id")
    private Kennel kennel;
}