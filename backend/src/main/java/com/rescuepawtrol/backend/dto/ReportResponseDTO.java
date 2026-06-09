package com.rescuepawtrol.backend.dto;

import java.util.List;

public class ReportResponseDTO {
    private KpiDTO kpi;
    private List<Integer> speciesDistribution;
    private List<Integer> adoptionsArray;
    private List<Integer> interventionsArray;

    public ReportResponseDTO() {
    }

    public ReportResponseDTO(KpiDTO kpi, List<Integer> speciesDistribution, List<Integer> adoptionsArray, List<Integer> interventionsArray) {
        this.kpi = kpi;
        this.speciesDistribution = speciesDistribution;
        this.adoptionsArray = adoptionsArray;
        this.interventionsArray = interventionsArray;
    }


    public KpiDTO getKpi() {
        return kpi;
    }

    public void setKpi(KpiDTO kpi) {
        this.kpi = kpi;
    }

    public List<Integer> getSpeciesDistribution() {
        return speciesDistribution;
    }

    public void setSpeciesDistribution(List<Integer> speciesDistribution) {
        this.speciesDistribution = speciesDistribution;
    }

    public List<Integer> getAdoptionsArray() {
        return adoptionsArray;
    }

    public void setAdoptionsArray(List<Integer> adoptionsArray) {
        this.adoptionsArray = adoptionsArray;
    }

    public List<Integer> getInterventionsArray() {
        return interventionsArray;
    }

    public void setInterventionsArray(List<Integer> interventionsArray) {
        this.interventionsArray = interventionsArray;
    }
}