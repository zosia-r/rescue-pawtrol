package com.rescuepawtrol.backend.repository;

import com.rescuepawtrol.backend.model.AdoptionCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AdoptionCardRepository extends JpaRepository<AdoptionCard, Long> {
    List<AdoptionCard> findAllBySubmissionDateBetween(LocalDate start, LocalDate end);
}