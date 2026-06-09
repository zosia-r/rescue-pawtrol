package com.rescuepawtrol.backend.service;

import com.rescuepawtrol.backend.dto.KpiDTO;
import com.rescuepawtrol.backend.dto.ReportResponseDTO;
import com.rescuepawtrol.backend.model.AdoptionCard;
import com.rescuepawtrol.backend.model.Intervention;
import com.rescuepawtrol.backend.repository.AdoptionCardRepository;
import com.rescuepawtrol.backend.repository.AnimalRepository;
import com.rescuepawtrol.backend.repository.InterventionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportService {

    private final AdoptionCardRepository adoptionCardRepository;
    private final InterventionRepository interventionRepository;
    private final AnimalRepository animalRepository;

    public ReportService(AdoptionCardRepository adoptionCardRepository,
                         InterventionRepository interventionRepository,
                         AnimalRepository animalRepository) {
        this.adoptionCardRepository = adoptionCardRepository;
        this.interventionRepository = interventionRepository;
        this.animalRepository = animalRepository;
    }

    public ReportResponseDTO generateReportData(LocalDate start, LocalDate end) {
        ReportResponseDTO response = new ReportResponseDTO();

        // --- 1. POBIERANIE DANYCH Z BAZY (OBECNY OKRES) ---
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        List<AdoptionCard> currentAdoptions = adoptionCardRepository.findAllBySubmissionDateBetween(start, end);
        List<Intervention> currentInterventions = interventionRepository.findAllByReportTimeBetween(startDateTime, endDateTime);

        // --- 2. POBIERANIE DANYCH Z BAZY (POPRZEDNI OKRES - DO TRENDÓW) ---
        long daysBetween = ChronoUnit.DAYS.between(start, end) + 1;
        LocalDate previousStart = start.minusDays(daysBetween);
        LocalDate previousEnd = start.minusDays(1);

        List<AdoptionCard> previousAdoptions = adoptionCardRepository.findAllBySubmissionDateBetween(previousStart, previousEnd);
        List<Intervention> previousInterventions = interventionRepository.findAllByReportTimeBetween(previousStart.atStartOfDay(), previousEnd.atTime(23, 59, 59));

        // --- 3. WYLICZANIE KPI (Twarde dane) ---
        KpiDTO kpi = new KpiDTO();

        kpi.setTotalAdoptions(currentAdoptions.size());
        kpi.setTrendAdoptions(calculateTrend(currentAdoptions.size(), previousAdoptions.size()));

        kpi.setInterventions(currentInterventions.size());
        kpi.setTrendInterventions(calculateTrend(currentInterventions.size(), previousInterventions.size()));

        // Zliczanie wszystkich zwierząt oraz tych gotowych do adopcji
        kpi.setTotalAnimals((int) animalRepository.count());
        long availableAnimals = animalRepository.countByAdoptionStatusIgnoreCase("Available");
        kpi.setTrendAnimals(availableAnimals + " ready for adoption");

        // Zliczanie zwierząt na kwarantannie
        long quarantinedAnimals = animalRepository.countByIsQuarantinedTrue();
        kpi.setAvgStay((int) quarantinedAnimals);

        response.setKpi(kpi);

        // --- 4. WYKRES LINIOWY (Zgruowane miesiące) ---
        List<Integer> adoptionsArray = new ArrayList<>();
        List<Integer> interventionsArray = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = YearMonth.from(end).minusMonths(i);

            long adoptionsInMonth = currentAdoptions.stream()
                    .filter(a -> a.getSubmissionDate() != null && YearMonth.from(a.getSubmissionDate()).equals(targetMonth))
                    .count();
            adoptionsArray.add((int) adoptionsInMonth);

            long interventionsInMonth = currentInterventions.stream()
                    .filter(inv -> inv.getReportTime() != null && YearMonth.from(inv.getReportTime()).equals(targetMonth))
                    .count();
            interventionsArray.add((int) interventionsInMonth);
        }

        response.setAdoptionsArray(adoptionsArray);
        response.setInterventionsArray(interventionsArray);

        // --- 5. WYKRES KOŁOWY Z BAZY ---
        long dogs = animalRepository.countBySpeciesIgnoreCase("Dog");
        long cats = animalRepository.countBySpeciesIgnoreCase("Cat");
        long rabbits = animalRepository.countBySpeciesIgnoreCase("Rabbit");
        long birds = animalRepository.countBySpeciesIgnoreCase("Bird");

        // Kolejność zgadza się z etykietami Vue: ['Dogs', 'Cats', 'Rabbits', 'Birds']
        response.setSpeciesDistribution(Arrays.asList((int) dogs, (int) cats, (int) rabbits, (int) birds));

        return response;
    }

    private String calculateTrend(int current, int previous) {
        if (previous == 0) {
            return current > 0 ? "+100" : "0";
        }
        double percentageChange = ((double) (current - previous) / previous) * 100;
        long roundedChange = Math.round(percentageChange);

        return roundedChange > 0 ? "+" + roundedChange : String.valueOf(roundedChange);
    }
}