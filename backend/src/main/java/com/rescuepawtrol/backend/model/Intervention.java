package com.rescuepawtrol.backend.model;

import com.rescuepawtrol.backend.model.enums.InterventionStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "intervention")
@Data
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    private LocalDateTime reportTime;

    @Enumerated(EnumType.STRING)
    private InterventionStatus status;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Employee driver;
}