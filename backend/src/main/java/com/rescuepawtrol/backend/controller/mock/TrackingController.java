package com.rescuepawtrol.backend.controller.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final double[][] patrolRoute = {
            {51.105000, 17.035000},
            {51.107883, 17.038538},
            {51.110000, 17.030000},
            {51.102000, 17.025000}
    };

    private int currentTargetIndex = 1;
    private double currentLat = patrolRoute[0][0];
    private double currentLng = patrolRoute[0][1];

    private final double SPEED = 0.0002;

    @GetMapping("/unit1")
    public Map<String, Double> getCarLocation() {
        double targetLat = patrolRoute[currentTargetIndex][0];
        double targetLng = patrolRoute[currentTargetIndex][1];

        double dLat = targetLat - currentLat;
        double dLng = targetLng - currentLng;
        double distanceToTarget = Math.sqrt(dLat * dLat + dLng * dLng);

        if (distanceToTarget < SPEED) {
            currentLat = targetLat;
            currentLng = targetLng;

            currentTargetIndex = (currentTargetIndex + 1) % patrolRoute.length;
        } else {
            double ratio = SPEED / distanceToTarget;
            currentLat += dLat * ratio;
            currentLng += dLng * ratio;
        }

        Map<String, Double> location = new HashMap<>();
        location.put("latitude", currentLat);
        location.put("longitude", currentLng);

        return location;
    }
}