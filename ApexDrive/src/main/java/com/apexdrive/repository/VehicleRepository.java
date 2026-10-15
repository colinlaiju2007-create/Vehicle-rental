package com.apexdrive.repository;

import com.apexdrive.model.Vehicle;
import com.apexdrive.model.VehicleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA Repository for Vehicle entities.
 * Demonstrates repository abstraction layer and custom JPQL queries.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByCategory(VehicleCategory category);

    List<Vehicle> findByAvailableTrue();

    List<Vehicle> findByCategoryAndAvailableTrue(VehicleCategory category);

    /**
     * Finds vehicles that are currently marked as fleet-available AND do not have any
     * conflicting non-cancelled bookings overlapping the requested date range.
     * 
     * Overlap condition:
     * (b.pickupDate <= :returnDate) AND (b.returnDate >= :pickupDate)
     */
    @Query("""
        SELECT v FROM Vehicle v 
        WHERE v.available = true 
        AND v.id NOT IN (
            SELECT b.vehicle.id FROM Booking b 
            WHERE b.status != com.apexdrive.model.BookingStatus.CANCELLED
            AND b.pickupDate <= :returnDate 
            AND b.returnDate >= :pickupDate
        )
        ORDER BY v.dailyPrice ASC
    """)
    List<Vehicle> findAvailableVehiclesForDates(
            @Param("pickupDate") LocalDate pickupDate,
            @Param("returnDate") LocalDate returnDate
    );

    /**
     * Filters available vehicles by category, price limit, and date availability.
     */
    @Query("""
        SELECT v FROM Vehicle v 
        WHERE v.available = true 
        AND (:category IS NULL OR v.category = :category)
        AND (:maxPrice IS NULL OR v.dailyPrice <= :maxPrice)
        AND v.id NOT IN (
            SELECT b.vehicle.id FROM Booking b 
            WHERE b.status != com.apexdrive.model.BookingStatus.CANCELLED
            AND b.pickupDate <= :returnDate 
            AND b.returnDate >= :pickupDate
        )
        ORDER BY v.dailyPrice ASC
    """)
    List<Vehicle> filterAvailableVehicles(
            @Param("category") VehicleCategory category,
            @Param("maxPrice") Double maxPrice,
            @Param("pickupDate") LocalDate pickupDate,
            @Param("returnDate") LocalDate returnDate
    );
}
