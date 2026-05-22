package com.rescuepawtrol.backend.model;

import com.rescuepawtrol.backend.model.enums.KennelType;
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
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private KennelType type;

    @Transient
    public String getSectorName() {
        if (type == null) return "Other";
        return switch (type) {
            case DOGS          -> "Sector B - Dogs";
            case CATS          -> "Sector A - Cats";
            case OTHER         -> "Other Animals";
            case ISOLATION_WARD -> "Isolation Ward";
            case QUARANTINE    -> "Quarantine";
        };
    }
}