package com.rescuepawtrol.backend.controller.mock;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/driver")
@CrossOrigin(origins = "*") // Dostosuj do swoich potrzeb bezpieczeństwa CORS
public class TrackingController {

    // Start z pozycji bazy schroniska
    private double latitude = 51.110000;
    private double longitude = 17.030000;
    
    // Prędkość poruszania się w stopniach na krok (~0.0003 stopnia na krok daje zbliżoną dynamikę)
    private static final double STEP_SIZE = 0.0003;
    private final Random random = new Random();

    // Aktualny kąt poruszania się w radianach
    private double currentHeading = random.nextDouble() * 2 * Math.PI;

    @GetMapping("/location")
    public synchronized Map<String, Double> getLiveLocation() {
        // Zmiana kierunku o mały losowy kąt (od -45 do +45 stopni), aby ruch był płynniejszy niż totalnie losowe skoki
        double angleChange = (random.nextDouble() - 0.5) * (Math.PI / 2);
        currentHeading += angleChange;

        // Wyliczenie nowej pozycji
        latitude += Math.sin(currentHeading) * STEP_SIZE;
        longitude += Math.cos(currentHeading) * STEP_SIZE;

        // Ograniczenie ruchu do okolic Wrocławia, aby autko nie uciekło z mapy
        if (latitude < 51.05 || latitude > 51.15 || longitude < 16.95 || longitude > 17.15) {
            // Zawróć w stronę bazy
            double toBaseLat = 51.110000 - latitude;
            double toBaseLng = 17.030000 - longitude;
            currentHeading = Math.atan2(toBaseLat, toBaseLng);
        }

        Map<String, Double> coords = new HashMap<>();
        coords.put("latitude", latitude);
        coords.put("longitude", longitude);
        return coords;
    }
}