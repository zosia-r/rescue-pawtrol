package com.rescuepawtrol.backend.controller;

import com.rescuepawtrol.backend.repository.AnimalRepository;
import com.rescuepawtrol.backend.repository.InterventionRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final AnimalRepository animalRepository;
    private final InterventionRepository interventionRepository;

    public ReportController(AnimalRepository animalRepository, InterventionRepository interventionRepository) {
        this.animalRepository = animalRepository;
        this.interventionRepository = interventionRepository;
    }

    @GetMapping("/animals-by-species")
    public Map<String, Long> getAnimalsBySpecies() {
        List<Object[]> results = animalRepository.countBySpecies();
        return results.stream().collect(Collectors.toMap(
                obj -> (String) obj[0],
                obj -> (Long) obj[1]
        ));
    }

    @GetMapping("/interventions-by-date")
    public List<Map<String, Object>> getInterventionsByDate(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start + "T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse(end + "T23:59:59");

        List<Object[]> results = interventionRepository.countByDateRange(startDate, endDate);

        return results.stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date", obj[0].toString());
            map.put("count", obj[1]);
            return map;
        }).collect(Collectors.toList());
    }
}