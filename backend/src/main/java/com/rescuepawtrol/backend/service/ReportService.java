package com.rescuepawtrol.backend.service;

import com.rescuepawtrol.backend.dto.KpiDTO;
import com.rescuepawtrol.backend.dto.ReportResponseDTO;
import com.rescuepawtrol.backend.model.AdoptionCard;
import com.rescuepawtrol.backend.model.MedicalRecord;
import com.rescuepawtrol.backend.repository.AdoptionCardRepository;
import com.rescuepawtrol.backend.repository.AnimalRepository;
import com.rescuepawtrol.backend.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportService {

    private final AdoptionCardRepository adoptionCardRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AnimalRepository animalRepository;

    public ReportService(AdoptionCardRepository adoptionCardRepository,
                         MedicalRecordRepository medicalRecordRepository,
                         AnimalRepository animalRepository) {
        this.adoptionCardRepository = adoptionCardRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.animalRepository = animalRepository;
    }

    public ReportResponseDTO generateReportData(LocalDate start, LocalDate end) {
        ReportResponseDTO response = new ReportResponseDTO();

        // --- 1. POBIERANIE DANYCH DLA KAFELKÓW (Wybrany zakres z kalendarza) ---
        List<AdoptionCard> currentAdoptions = adoptionCardRepository.findAllBySubmissionDateBetween(start, end);
        List<MedicalRecord> currentMedical = medicalRecordRepository.findAllByRecordDateBetween(start, end);

        // --- 2. POBIERANIE DANYCH DO TRENDÓW (Poprzedni analogiczny okres) ---
        long daysBetween = ChronoUnit.DAYS.between(start, end) + 1;
        LocalDate previousStart = start.minusDays(daysBetween);
        LocalDate previousEnd = start.minusDays(1);

        List<AdoptionCard> previousAdoptions = adoptionCardRepository.findAllBySubmissionDateBetween(previousStart, previousEnd);
        List<MedicalRecord> previousMedical = medicalRecordRepository.findAllByRecordDateBetween(previousStart, previousEnd);

        // --- 3. WYLICZANIE KPI ---
        KpiDTO kpi = new KpiDTO();

        kpi.setTotalAdoptions(currentAdoptions.size());
        kpi.setTrendAdoptions(calculateTrend(currentAdoptions.size(), previousAdoptions.size()));

        kpi.setInterventions(currentMedical.size());
        kpi.setTrendInterventions(calculateTrend(currentMedical.size(), previousMedical.size()));

        kpi.setTotalAnimals((int) animalRepository.count());
        long availableAnimals = animalRepository.countByAdoptionStatusIgnoreCase("Available");
        kpi.setTrendAnimals(availableAnimals + " ready for adoption");

        // Właściwe pole dla kwarantanny
        long quarantinedAnimals = animalRepository.countByIsQuarantinedTrue();
        kpi.setQuarantined((int) quarantinedAnimals);

        response.setKpi(kpi);

        // --- 4. WYKRES LINIOWY (Zawsze pełne 6 miesięcy wstecz od end_date) ---
        LocalDate sixMonthsAgo = end.minusMonths(5).withDayOfMonth(1); // Początek miesiąca 6 mies. temu

        List<AdoptionCard> chartAdoptions = adoptionCardRepository.findAllBySubmissionDateBetween(sixMonthsAgo, end);
        List<MedicalRecord> chartMedical = medicalRecordRepository.findAllByRecordDateBetween(sixMonthsAgo, end);

        List<Integer> adoptionsArray = new ArrayList<>();
        List<Integer> interventionsArray = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = YearMonth.from(end).minusMonths(i);

            long adoptionsInMonth = chartAdoptions.stream()
                    .filter(a -> a.getSubmissionDate() != null && YearMonth.from(a.getSubmissionDate()).equals(targetMonth))
                    .count();
            adoptionsArray.add((int) adoptionsInMonth);

            long medicalInMonth = chartMedical.stream()
                    .filter(m -> m.getRecordDate() != null && YearMonth.from(m.getRecordDate()).equals(targetMonth))
                    .count();
            interventionsArray.add((int) medicalInMonth);
        }

        response.setAdoptionsArray(adoptionsArray);
        response.setInterventionsArray(interventionsArray);

        // --- 5. WYKRES KOŁOWY ---
        long dogs = animalRepository.countBySpeciesIgnoreCase("Dog");
        long cats = animalRepository.countBySpeciesIgnoreCase("Cat");
        long rabbits = animalRepository.countBySpeciesIgnoreCase("Rabbit");
        long birds = animalRepository.countBySpeciesIgnoreCase("Bird");

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