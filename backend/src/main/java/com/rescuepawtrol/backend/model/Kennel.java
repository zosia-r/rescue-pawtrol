package com.rescuepawtrol.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "kennel")
@Data
public class Kennel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private Integer maxCapacity;
    private String zone;
}