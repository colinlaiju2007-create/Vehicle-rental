package com.apexdrive.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

/**
 * Sports Vehicle subclass demonstrating OOP Inheritance and Polymorphism.
 * 
 * Specializes in high-performance automotive dynamics with track readiness features.
 */
@Entity
@DiscriminatorValue("SPORTS")
public class SportsVehicle extends Vehicle {

    @Column(name = "top_speed_kph")
    private Integer topSpeedKph = 300;

    @Column(name = "track_package")
    private Boolean trackPackage = true;

    @Column(name = "performance_insurance_fee")
    private Double performanceInsuranceFee = 40.0;

    public SportsVehicle() {
        super();
        setCategory(VehicleCategory.SPORTS);
    }

    public SportsVehicle(String name, Double dailyPrice, String specifications,
                         String transmission, Integer seatingCapacity, String fuelType,
                         String imageUrl, Boolean available, Integer topSpeedKph,
                         Boolean trackPackage, Double performanceInsuranceFee) {
        super(name, VehicleCategory.SPORTS, dailyPrice, specifications, transmission,
              seatingCapacity, fuelType, imageUrl, available, 250.0);
        this.topSpeedKph = topSpeedKph != null ? topSpeedKph : 300;
        this.trackPackage = trackPackage != null ? trackPackage : true;
        this.performanceInsuranceFee = performanceInsuranceFee != null ? performanceInsuranceFee : 40.0;
    }

    /**
     * Polymorphic Override:
     * Calculates sports performance cost with specialized telemetry & track-grade insurance.
     */
    @Override
    public double calculateRentalCost(long rentalDays) {
        if (rentalDays < 1) rentalDays = 1;
        double baseRate = getDailyPrice() * rentalDays;
        
        // 8% discount for rentals >= 4 days
        if (rentalDays >= 4) {
            baseRate *= 0.92;
        }
        
        double total = baseRate + (this.performanceInsuranceFee != null ? this.performanceInsuranceFee : 40.0);
        return Math.round(total * 100.0) / 100.0;
    }

    @Override
    public String getSpecialPerks() {
        return "Apex Sport Telemetry, Sport Exhaust Mode, Ceramic Brake Insurance Coverage, Pirelli P-Zero Tyres.";
    }

    @Override
    @Transient
    public String getVehicleType() {
        return "SPORTS";
    }

    public Integer getTopSpeedKph() {
        return topSpeedKph;
    }

    public void setTopSpeedKph(Integer topSpeedKph) {
        this.topSpeedKph = topSpeedKph;
    }

    public Boolean getTrackPackage() {
        return trackPackage;
    }

    public void setTrackPackage(Boolean trackPackage) {
        this.trackPackage = trackPackage;
    }

    public Double getPerformanceInsuranceFee() {
        return performanceInsuranceFee;
    }

    public void setPerformanceInsuranceFee(Double performanceInsuranceFee) {
        this.performanceInsuranceFee = performanceInsuranceFee;
    }
}
