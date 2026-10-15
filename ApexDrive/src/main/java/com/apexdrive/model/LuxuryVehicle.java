package com.apexdrive.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

/**
 * Luxury Vehicle subclass demonstrating OOP Inheritance and Polymorphism.
 * 
 * Inherits core attributes from Vehicle and introduces concierge service fees
 * and VIP insurance amenities.
 */
@Entity
@DiscriminatorValue("LUXURY")
public class LuxuryVehicle extends Vehicle {

    @Column(name = "concierge_fee")
    private Double conciergeFee = 50.0; // Flat luxury VIP concierge fee

    @Column(name = "chauffeur_available")
    private Boolean chauffeurAvailable = true;

    public LuxuryVehicle() {
        super();
        setCategory(VehicleCategory.LUXURY);
    }

    public LuxuryVehicle(String name, Double dailyPrice, String specifications,
                         String transmission, Integer seatingCapacity, String fuelType,
                         String imageUrl, Boolean available, Double conciergeFee, Boolean chauffeurAvailable) {
        super(name, VehicleCategory.LUXURY, dailyPrice, specifications, transmission,
              seatingCapacity, fuelType, imageUrl, available, 500.0); // 500 km/day generous limit
        this.conciergeFee = conciergeFee != null ? conciergeFee : 50.0;
        this.chauffeurAvailable = chauffeurAvailable != null ? chauffeurAvailable : true;
    }

    /**
     * Polymorphic Override:
     * Calculates cost with complimentary VIP executive insurance and flat concierge care.
     * Offers 10% privilege discount for rentals exceeding 5 days.
     */
    @Override
    public double calculateRentalCost(long rentalDays) {
        if (rentalDays < 1) rentalDays = 1;
        double baseRate = getDailyPrice() * rentalDays;
        
        // 10% discount for luxury rentals >= 5 days
        if (rentalDays >= 5) {
            baseRate *= 0.90;
        }
        
        // Luxury vehicles include one-time VIP concierge booking preparation
        double total = baseRate + (this.conciergeFee != null ? this.conciergeFee : 50.0);
        return Math.round(total * 100.0) / 100.0;
    }

    @Override
    public String getSpecialPerks() {
        return "Apex Black VIP Concierge, Zero-Deductible Executive Insurance, Airport Fast-Track Delivery.";
    }

    @Override
    @Transient
    public String getVehicleType() {
        return "LUXURY";
    }

    public Double getConciergeFee() {
        return conciergeFee;
    }

    public void setConciergeFee(Double conciergeFee) {
        this.conciergeFee = conciergeFee;
    }

    public Boolean getChauffeurAvailable() {
        return chauffeurAvailable;
    }

    public void setChauffeurAvailable(Boolean chauffeurAvailable) {
        this.chauffeurAvailable = chauffeurAvailable;
    }
}
