package com.apexdrive.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Base Vehicle Entity representing a rental fleet item.
 * 
 * OOP Principles Demonstrated:
 * 1. Encapsulation: Private state variables accessed exclusively through public getters/setters
 *    with data validation guards.
 * 2. Inheritance: Serves as the superclass for LuxuryVehicle, SportsVehicle, and ElectricVehicle.
 * 3. Polymorphism: Implements the RentalCalculable interface with customizable behavior.
 */
@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("STANDARD")
public class Vehicle implements RentalCalculable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vehicle name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleCategory category;

    @NotNull(message = "Daily price is required")
    @DecimalMin(value = "1.0", message = "Daily price must be greater than zero")
    @Column(name = "daily_price", nullable = false)
    private Double dailyPrice;

    @Column(columnDefinition = "TEXT")
    private String specifications;

    @Column(nullable = false)
    private String transmission; // Automatic, Manual, Dual-Clutch

    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Column(name = "fuel_type", nullable = false)
    private String fuelType; // Petrol, Diesel, Hybrid, Electric

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(name = "mileage_limit_km_per_day")
    private Double mileageLimitKmPerDay = 300.0; // Standard daily limit

    // Default constructor required by JPA
    public Vehicle() {
    }

    // Parameterized constructor for object instantiation
    public Vehicle(String name, VehicleCategory category, Double dailyPrice, String specifications,
                   String transmission, Integer seatingCapacity, String fuelType, String imageUrl,
                   Boolean available, Double mileageLimitKmPerDay) {
        this.name = name;
        this.category = category;
        this.dailyPrice = dailyPrice;
        this.specifications = specifications;
        this.transmission = transmission;
        this.seatingCapacity = seatingCapacity;
        this.fuelType = fuelType;
        this.imageUrl = imageUrl;
        this.available = available != null ? available : true;
        this.mileageLimitKmPerDay = mileageLimitKmPerDay != null ? mileageLimitKmPerDay : 300.0;
    }

    /**
     * Polymorphic rental cost calculation.
     * Applies a 5% weekly volume discount if rented for 7 or more days.
     *
     * @param rentalDays Total duration of rental
     * @return Calculated cost
     */
    @Override
    public double calculateRentalCost(long rentalDays) {
        if (rentalDays < 1) {
            rentalDays = 1;
        }
        double baseRate = (this.dailyPrice != null ? this.dailyPrice : 0.0) * rentalDays;
        if (rentalDays >= 7) {
            // 5% standard long-term discount
            return Math.round((baseRate * 0.95) * 100.0) / 100.0;
        }
        return Math.round(baseRate * 100.0) / 100.0;
    }

    @Override
    public String getSpecialPerks() {
        return "Standard comprehensive insurance, 24/7 roadside assistance, 300 km/day mileage limit.";
    }

    // --- Encapsulation: Getters and Setters with validation checks ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VehicleCategory getCategory() {
        return category;
    }

    public void setCategory(VehicleCategory category) {
        this.category = category;
    }

    public Double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(Double dailyPrice) {
        if (dailyPrice != null && dailyPrice < 0) {
            throw new IllegalArgumentException("Daily price cannot be negative");
        }
        this.dailyPrice = dailyPrice;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Double getMileageLimitKmPerDay() {
        return mileageLimitKmPerDay;
    }

    public void setMileageLimitKmPerDay(Double mileageLimitKmPerDay) {
        this.mileageLimitKmPerDay = mileageLimitKmPerDay;
    }

    @Transient
    public String getVehicleType() {
        return "STANDARD";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", dailyPrice=" + dailyPrice +
                ", available=" + available +
                '}';
    }
}
