package com.rescuepawtrol.backend.controller;

import com.rescuepawtrol.backend.model.Animal;
import com.rescuepawtrol.backend.model.AdoptionStatusHistory;
import com.rescuepawtrol.backend.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    record KennelAssignmentRequest(Long kennelId) {}

    @GetMapping
    public List<Animal> getAllAnimals() {
        return animalService.getAllAnimals();
    }

    @PostMapping
    public Animal addAnimal(@RequestBody Animal animal) {
        return animalService.saveAnimal(animal);
    }

    @PatchMapping("/{id}/kennel")
    public Animal assignKennel(@PathVariable Long id,
                               @RequestBody Map<String, Long> body) {
        return animalService.assignKennel(id, body.get("kennelId"));
    }

    @PatchMapping("/{id}/adoption-status")
    public Animal updateAdoptionStatus(@PathVariable Long id,
                                       @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return animalService.updateAdoptionStatus(id, status);
    }
}