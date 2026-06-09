package com.rescuepawtrol.backend.dto;

public class KpiDTO {
    private int totalAnimals;
    private String trendAnimals;
    private int totalAdoptions;
    private String trendAdoptions;
    private int interventions;
    private String trendInterventions;
    private int avgStay;

    public KpiDTO() {
    }

    public KpiDTO(int totalAnimals, String trendAnimals, int totalAdoptions, String trendAdoptions, int interventions, String trendInterventions, int avgStay) {
        this.totalAnimals = totalAnimals;
        this.trendAnimals = trendAnimals;
        this.totalAdoptions = totalAdoptions;
        this.trendAdoptions = trendAdoptions;
        this.interventions = interventions;
        this.trendInterventions = trendInterventions;
        this.avgStay = avgStay;
    }


    public int getTotalAnimals() {
        return totalAnimals;
    }

    public void setTotalAnimals(int totalAnimals) {
        this.totalAnimals = totalAnimals;
    }

    public String getTrendAnimals() {
        return trendAnimals;
    }

    public void setTrendAnimals(String trendAnimals) {
        this.trendAnimals = trendAnimals;
    }

    public int getTotalAdoptions() {
        return totalAdoptions;
    }

    public void setTotalAdoptions(int totalAdoptions) {
        this.totalAdoptions = totalAdoptions;
    }

    public String getTrendAdoptions() {
        return trendAdoptions;
    }

    public void setTrendAdoptions(String trendAdoptions) {
        this.trendAdoptions = trendAdoptions;
    }

    public int getInterventions() {
        return interventions;
    }

    public void setInterventions(int interventions) {
        this.interventions = interventions;
    }

    public String getTrendInterventions() {
        return trendInterventions;
    }

    public void setTrendInterventions(String trendInterventions) {
        this.trendInterventions = trendInterventions;
    }

    public int getAvgStay() {
        return avgStay;
    }

    public void setAvgStay(int avgStay) {
        this.avgStay = avgStay;
    }
}