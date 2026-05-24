package com.rescuepawtrol.backend.controller;

import com.rescuepawtrol.backend.model.Intervention;
import com.rescuepawtrol.backend.model.enums.InterventionStatus;
import com.rescuepawtrol.backend.repository.InterventionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/interventions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InterventionController {

    private final InterventionRepository interventionRepository;

    @GetMapping
    public ResponseEntity<List<Intervention>> getAllInterventions() {
        return ResponseEntity.ok(interventionRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Intervention> createIntervention(@RequestBody Intervention request) {
        request.setReportTime(LocalDateTime.now());

        request.setStatus(InterventionStatus.values()[0]);

        Intervention savedIntervention = interventionRepository.save(request);
        return ResponseEntity.ok(savedIntervention);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'MANAGER')")
    public ResponseEntity<Void> deleteIntervention(@PathVariable Long id) {
        if (!interventionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        interventionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}