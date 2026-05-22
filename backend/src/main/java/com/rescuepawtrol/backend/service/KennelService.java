package com.rescuepawtrol.backend.service;

import com.rescuepawtrol.backend.model.Kennel;
import com.rescuepawtrol.backend.repository.KennelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KennelService {
    private final KennelRepository kennelRepository;

    public List<Kennel> getAllKennels() {
        return kennelRepository.findAll();
    }

    public Kennel saveKennel(Kennel kennel) {
        return kennelRepository.save(kennel);
    }
}