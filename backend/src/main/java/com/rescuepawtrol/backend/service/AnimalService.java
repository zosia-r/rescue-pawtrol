package com.rescuepawtrol.backend.service;

import com.rescuepawtrol.backend.model.Animal;
import com.rescuepawtrol.backend.model.Kennel;
import com.rescuepawtrol.backend.model.enums.KennelType;
import com.rescuepawtrol.backend.repository.AnimalRepository;
import com.rescuepawtrol.backend.repository.KennelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.rescuepawtrol.backend.model.enums.KennelType.*;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final KennelRepository kennelRepository;

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public Animal saveAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    public Animal assignKennel(Long animalId, Long kennelId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Animal not found: " + animalId));

        if (kennelId == null) {
            // Wyciąganie zwierzęcia z klatki
            if (animal.getKennel() != null
                    && animal.getKennel().getType() == KennelType.QUARANTINE) {
                animal.setIsQuarantined(false);
            }
            animal.setKennel(null);
        } else {
            Kennel kennel = kennelRepository.findById(kennelId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Kennel not found: " + kennelId));

            validateSpecies(animal, kennel);

            // Wsadzanie do klatki kwarantanny
            if (kennel.getType() == KennelType.QUARANTINE) {
                animal.setIsQuarantined(true);
            }

            animal.setKennel(kennel);
        }

        return animalRepository.save(animal);
    }

    private void validateSpecies(Animal animal, Kennel kennel) {
        KennelType type = kennel.getType();
        String species = animal.getSpecies();

        boolean valid = switch (type) {
            case DOGS          -> "Dog".equalsIgnoreCase(species);
            case CATS          -> "Cat".equalsIgnoreCase(species);
            case OTHER         -> !"Dog".equalsIgnoreCase(species)
                    && !"Cat".equalsIgnoreCase(species);
            case QUARANTINE,
                 ISOLATION_WARD -> true; // brak ograniczeń gatunkowych
        };

        if (!valid) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Kennel %s (%s) does not accept species: %s"
                            .formatted(kennel.getCode(), type, species)
            );
        }
    }
}