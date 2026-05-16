package com.rescuepawtrol.backend.repository;
import com.rescuepawtrol.backend.model.AdoptionCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptionCardRepository extends JpaRepository<AdoptionCard, Long> {}