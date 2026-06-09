package com.rescuepawtrol.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rescuepawtrol.backend.model.enums.InterventionStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "intervention")
@Data
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String description;

    private Double latitude;
    private Double longitude;

    private LocalDateTime reportTime;

    @Enumerated(EnumType.STRING)
    private InterventionStatus status;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Employee driver;

    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RoutePoint> routePoints;
}