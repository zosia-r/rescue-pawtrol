package com.rescuepawtrol.backend.service;

import com.rescuepawtrol.backend.model.AdoptionStatusHistory;
import com.rescuepawtrol.backend.repository.AdoptionStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionStatusHistoryService {

    private final AdoptionStatusHistoryRepository adoptionStatusHistoryRepository;

    public AdoptionStatusHistory save(AdoptionStatusHistory adoptionStatusHistory) {
        return adoptionStatusHistoryRepository.save(adoptionStatusHistory);
    }

    public List<AdoptionStatusHistory> getHistoryByAnimalId(Long animalId) {
        return adoptionStatusHistoryRepository.findByAnimalIdOrderByDateDesc(animalId);
    }
}
