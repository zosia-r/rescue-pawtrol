package com.rescuepawtrol.backend.model;

import com.rescuepawtrol.backend.model.enums.RoutePointType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "route_point")
@Data
public class RoutePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer stepOrder;

    private String address;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private RoutePointType pointType;

    @ManyToOne
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;
}