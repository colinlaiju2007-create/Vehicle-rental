package com.apexdrive.dto;

import com.apexdrive.model.VehicleCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehicleDto {

    private Long id;

    @NotBlank(message = "Vehicle name is required")
    private String name;

    @NotNull(message = "Vehicle category is required")
    private VehicleCategory category;

    @NotNull(message = "Daily price is required")
    @DecimalMin(value = "1.0", message = "Daily price must be at least 1.0")
    private Double dailyPrice;

    private String specifications;

    @NotBlank(message = "Transmission is required")
    private String transmission;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    private Integer seatingCapacity;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    private String imageUrl;

    private Boolean available = true;

    private Double mileageLimitKmPerDay;

    // Subclass specific fields
    private Double conciergeFee;
    private Boolean chauffeurAvailable;
    private Integer topSpeedKph;
    private Boolean trackPackage;
    private Double performanceInsuranceFee;
    private Integer batteryRangeKm;
    private Boolean fastChargingSupport;

    public VehicleDto() {
    }

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
}
