package com.apexdrive.repository;

import com.apexdrive.model.Booking;
import com.apexdrive.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Booking entities.
 * Includes explicit queries for overlapping reservation detection and administrative aggregates.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    Optional<Booking> findByBookingReferenceAndCustomerEmailIgnoreCase(String bookingReference, String customerEmail);

    List<Booking> findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByStatus(BookingStatus status);

    /**
     * Determines whether a vehicle is booked by any non-cancelled reservation in a specified window.
     */
    @Query("""
        SELECT b FROM Booking b 
        WHERE b.vehicle.id = :vehicleId 
        AND b.status != com.apexdrive.model.BookingStatus.CANCELLED
        AND b.pickupDate <= :returnDate 
        AND b.returnDate >= :pickupDate
    """)
    List<Booking> findOverlappingBookings(
            @Param("vehicleId") Long vehicleId,
            @Param("pickupDate") LocalDate pickupDate,
            @Param("returnDate") LocalDate returnDate
    );

    /**
     * Aggregates total gross revenue generated from active/completed bookings.
     */
    @Query("""
        SELECT COALESCE(SUM(b.totalRentalCost), 0.0) 
        FROM Booking b 
        WHERE b.status != com.apexdrive.model.BookingStatus.CANCELLED
    """)
    Double calculateGrossRevenue();
}
