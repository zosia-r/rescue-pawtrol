package com.rescuepawtrol.backend.repository;
import com.rescuepawtrol.backend.model.Kennel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KennelRepository extends JpaRepository<Kennel, Long> {}