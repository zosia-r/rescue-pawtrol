package com.rescuepawtrol.backend.repository;
import com.rescuepawtrol.backend.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    @Query("SELECT a.species, COUNT(a) FROM Animal a GROUP BY a.species")
    List<Object[]> countBySpecies();

    long countBySpeciesIgnoreCase(String species);

    long countByAdoptionStatusIgnoreCase(String status);

    long countByIsQuarantinedTrue();
}
