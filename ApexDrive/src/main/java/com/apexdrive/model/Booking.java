package com.apexdrive.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Booking entity representing a vehicle reservation in the ApexDrive platform.
 * 
 * Demonstrates:
 * - Encapsulation of booking lifecycle rules and state
 * - Object relational relationships (@ManyToOne Vehicle and Customer)
 * - Defensive business invariants (calculating durations, checking cancellation eligibility)
 */
@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_ref", columnList = "booking_reference", unique = true),
    @Index(name = "idx_vehicle_dates", columnList = "vehicle_id, pickup_date, return_date")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", nullable = false, unique = true, length = 32)
    private String bookingReference;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Vehicle is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "Pickup date is required")
    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @NotNull(message = "Return date is required")
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "rental_days", nullable = false)
    private Long rentalDays;

    @Column(name = "daily_rate_snapshot", nullable = false)
    private Double dailyRateSnapshot;

    @NotNull(message = "Total rental cost is required")
    @DecimalMin(value = "0.0", message = "Total cost cannot be negative")
    @Column(name = "total_rental_cost", nullable = false)
    private Double totalRentalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "special_requests")
    private String specialRequests;

    public Booking() {
    }

    public Booking(String bookingReference, Customer customer, Vehicle vehicle,
                   LocalDate pickupDate, LocalDate returnDate, Double totalRentalCost,
                   String specialRequests) {
        this.bookingReference = bookingReference;
        this.customer = customer;
        this.vehicle = vehicle;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.rentalDays = calculateDurationDays(pickupDate, returnDate);
        this.dailyRateSnapshot = vehicle != null ? vehicle.getDailyPrice() : 0.0;
        this.totalRentalCost = totalRentalCost;
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
        this.specialRequests = specialRequests;
    }

    /**
     * Helper calculation method for rental duration in days.
     * Guaranteed minimum of 1 day if pickup and return are same day.
     */
    public static long calculateDurationDays(LocalDate pickup, LocalDate returnD) {
        if (pickup == null || returnD == null) return 1;
        long days = ChronoUnit.DAYS.between(pickup, returnD);
        return days <= 0 ? 1 : days;
    }

    /**
     * Cancels the current reservation while preserving the complete historical record.
     * 
     * @param reason The reason provided for cancellation
     */
    public void cancelBooking(String reason) {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled.");
        }
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = (reason != null && !reason.isBlank()) ? reason : "Customer requested cancellation";
    }

    /**
     * Checks if this booking overlaps with a prospective date window.
     * Overlap occurs when: (this.pickupDate <= newReturn) AND (this.returnDate >= newPickup)
     */
    public boolean overlapsWith(LocalDate newPickup, LocalDate newReturn) {
        if (this.status == BookingStatus.CANCELLED) {
            return false; // Cancelled reservations do not block availability
        }
        return !(this.returnDate.isBefore(newPickup) || this.pickupDate.isAfter(newReturn));
    }

    // --- Encapsulation: Getters and Setters ---

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingReference, booking.bookingReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingReference);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", bookingReference='" + bookingReference + '\'' +
                ", vehicle=" + (vehicle != null ? vehicle.getName() : "null") +
                ", pickupDate=" + pickupDate +
                ", returnDate=" + returnDate +
                ", totalRentalCost=" + totalRentalCost +
                ", status=" + status +
                '}';
    }
}
