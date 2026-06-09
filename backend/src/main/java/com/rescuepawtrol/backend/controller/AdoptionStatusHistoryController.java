package com.rescuepawtrol.backend.controller;

import com.rescuepawtrol.backend.model.AdoptionStatusHistory;
import com.rescuepawtrol.backend.service.AdoptionStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-status-history")
@RequiredArgsConstructor
public class AdoptionStatusHistoryController {

    private final AdoptionStatusHistoryService adoptionStatusHistoryService;

    @PostMapping
    public AdoptionStatusHistory addStatusChange(@RequestBody AdoptionStatusHistory adoptionStatusHistory) {
        return adoptionStatusHistoryService.save(adoptionStatusHistory);
    }

    @GetMapping("/animal/{animalId}")
    public List<AdoptionStatusHistory> getStatusHistoryByAnimal(@PathVariable Long animalId) {
        return adoptionStatusHistoryService.getHistoryByAnimalId(animalId);
    }
}
