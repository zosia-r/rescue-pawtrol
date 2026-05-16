package com.rescuepawtrol.backend.controller;

import com.rescuepawtrol.backend.model.Kennel;
import com.rescuepawtrol.backend.service.KennelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kennels")
@RequiredArgsConstructor
public class KennelController {

    private final KennelService kennelService;

    @GetMapping
    public List<Kennel> getAllKennels() {
        return kennelService.getAllKennels();
    }
}