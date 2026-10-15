package com.apexdrive.dto;

public class PriceCalculationResponse {

    private Long vehicleId;
    private String vehicleName;
    private Double dailyPrice;
    private long rentalDays;
    private double baseTotal;
    private double finalTotal;
    private double discountOrAdjustment;
    private String perks;
    private boolean availableForDates;

    public PriceCalculationResponse() {
    }

    public PriceCalculationResponse(Long vehicleId, String vehicleName, Double dailyPrice,
                                    long rentalDays, double baseTotal, double finalTotal,
                                    double discountOrAdjustment, String perks, boolean availableForDates) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.dailyPrice = dailyPrice;
        this.rentalDays = rentalDays;
        this.baseTotal = baseTotal;
        this.finalTotal = finalTotal;
        this.discountOrAdjustment = discountOrAdjustment;
        this.perks = perks;
        this.availableForDates = availableForDates;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public Double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(Double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public long getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(long rentalDays) {
        this.rentalDays = rentalDays;
    }

    public double getBaseTotal() {
        return baseTotal;
    }

    public void setBaseTotal(double baseTotal) {
        this.baseTotal = baseTotal;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public double getDiscountOrAdjustment() {
        return discountOrAdjustment;
    }

    public void setDiscountOrAdjustment(double discountOrAdjustment) {
        this.discountOrAdjustment = discountOrAdjustment;
    }

    public String getPerks() {
        return perks;
    }

    public void setPerks(String perks) {
        this.perks = perks;
    }

    public boolean isAvailableForDates() {
        return availableForDates;
    }

    public void setAvailableForDates(boolean availableForDates) {
        this.availableForDates = availableForDates;
    }
}
