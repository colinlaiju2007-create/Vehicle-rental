package com.apexdrive.dto;

import com.apexdrive.model.Booking;
import com.apexdrive.model.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing detailed booking confirmation and history.
 */
public class BookingResponse {

    private Long id;
    private String bookingReference;
    private Long vehicleId;
    private String vehicleName;
    private String vehicleCategory;
    private String vehicleImageUrl;
    private Double vehicleDailyPrice;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private Long rentalDays;
    private Double dailyRateSnapshot;
    private Double totalRentalCost;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private String specialRequests;
    private String vehiclePerks;

    public BookingResponse() {
    }

    public static BookingResponse fromEntity(Booking booking) {
        BookingResponse resp = new BookingResponse();
        resp.setId(booking.getId());
        resp.setBookingReference(booking.getBookingReference());
        
        if (booking.getVehicle() != null) {
            resp.setVehicleId(booking.getVehicle().getId());
            resp.setVehicleName(booking.getVehicle().getName());
            resp.setVehicleCategory(booking.getVehicle().getCategory() != null ? booking.getVehicle().getCategory().getDisplayName() : "Standard");
            resp.setVehicleImageUrl(booking.getVehicle().getImageUrl());
            resp.setVehicleDailyPrice(booking.getVehicle().getDailyPrice());
            resp.setVehiclePerks(booking.getVehicle().getSpecialPerks());
        }

        if (booking.getCustomer() != null) {
            resp.setCustomerName(booking.getCustomer().getFullName());
            resp.setCustomerEmail(booking.getCustomer().getEmail());
            resp.setCustomerPhone(booking.getCustomer().getPhone());
        }

        resp.setPickupDate(booking.getPickupDate());
        resp.setReturnDate(booking.getReturnDate());
        resp.setRentalDays(booking.getRentalDays());
        resp.setDailyRateSnapshot(booking.getDailyRateSnapshot());
        resp.setTotalRentalCost(booking.getTotalRentalCost());
        resp.setStatus(booking.getStatus());
        resp.setCreatedAt(booking.getCreatedAt());
        resp.setCancelledAt(booking.getCancelledAt());
        resp.setCancellationReason(booking.getCancellationReason());
        resp.setSpecialRequests(booking.getSpecialRequests());

        return resp;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
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

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    public String getVehicleImageUrl() {
        return vehicleImageUrl;
    }

    public void setVehicleImageUrl(String vehicleImageUrl) {
        this.vehicleImageUrl = vehicleImageUrl;
    }

    public Double getVehicleDailyPrice() {
        return vehicleDailyPrice;
    }

    public void setVehicleDailyPrice(Double vehicleDailyPrice) {
        this.vehicleDailyPrice = vehicleDailyPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Long rentalDays) {
        this.rentalDays = rentalDays;
    }

    public Double getDailyRateSnapshot() {
        return dailyRateSnapshot;
    }

    public void setDailyRateSnapshot(Double dailyRateSnapshot) {
        this.dailyRateSnapshot = dailyRateSnapshot;
    }

    public Double getTotalRentalCost() {
        return totalRentalCost;
    }

    public void setTotalRentalCost(Double totalRentalCost) {
        this.totalRentalCost = totalRentalCost;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getVehiclePerks() {
        return vehiclePerks;
    }

    public void setVehiclePerks(String vehiclePerks) {
        this.vehiclePerks = vehiclePerks;
    }
}
