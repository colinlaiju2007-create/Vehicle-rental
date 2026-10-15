package com.apexdrive.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

/**
 * Electric Vehicle subclass demonstrating OOP Inheritance and Polymorphism.
 * 
 * Includes eco-friendly zero-emissions features, charging network access,
 * and green technology rebates.
 */
@Entity
@DiscriminatorValue("ELECTRIC")
public class ElectricVehicle extends Vehicle {

    @Column(name = "battery_range_km")
    private Integer batteryRangeKm = 500;

    @Column(name = "fast_charging_support")
    private Boolean fastChargingSupport = true;

    @Column(name = "eco_discount_pct")
    private Double ecoDiscountPct = 7.0; // 7% green energy discount

    public ElectricVehicle() {
        super();
        setCategory(VehicleCategory.ELECTRIC);
    }

    public ElectricVehicle(String name, Double dailyPrice, String specifications,
                           String transmission, Integer seatingCapacity, String imageUrl,
                           Boolean available, Integer batteryRangeKm, Boolean fastChargingSupport) {
        super(name, VehicleCategory.ELECTRIC, dailyPrice, specifications, transmission,
              seatingCapacity, "Electric", imageUrl, available, 450.0);
        this.batteryRangeKm = batteryRangeKm != null ? batteryRangeKm : 500;
        this.fastChargingSupport = fastChargingSupport != null ? fastChargingSupport : true;
        this.ecoDiscountPct = 7.0;
    }

    /**
     * Polymorphic Override:
     * Automatically applies eco-green discount rebate and includes free unlimited supercharging.
     */
    @Override
    public double calculateRentalCost(long rentalDays) {
        if (rentalDays < 1) rentalDays = 1;
        double baseRate = getDailyPrice() * rentalDays;
        
        // Eco discount of 7% applied to all electric rentals
        double discount = baseRate * (ecoDiscountPct / 100.0);
        double total = baseRate - discount;

        // Additional 5% discount if 7+ days
        if (rentalDays >= 7) {
            total *= 0.95;
        }

        return Math.round(total * 100.0) / 100.0;
    }

    @Override
    public String getSpecialPerks() {
        return "Complimentary Unlimited Supercharging, Carbon-Neutral Driving Certificate, Free High-Occupancy Lane Access.";
    }

    @Override
    @Transient
    public String getVehicleType() {
        return "ELECTRIC";
    }

    public Integer getBatteryRangeKm() {
        return batteryRangeKm;
    }

    public void setBatteryRangeKm(Integer batteryRangeKm) {
        this.batteryRangeKm = batteryRangeKm;
    }

    public Boolean getFastChargingSupport() {
        return fastChargingSupport;
    }

    public void setFastChargingSupport(Boolean fastChargingSupport) {
        this.fastChargingSupport = fastChargingSupport;
    }

    public Double getEcoDiscountPct() {
        return ecoDiscountPct;
    }

    public void setEcoDiscountPct(Double ecoDiscountPct) {
        this.ecoDiscountPct = ecoDiscountPct;
    }
}
