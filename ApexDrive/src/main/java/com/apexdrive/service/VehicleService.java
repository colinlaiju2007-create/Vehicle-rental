package com.apexdrive.service;

import com.apexdrive.dto.VehicleDto;
import com.apexdrive.model.Vehicle;
import com.apexdrive.model.VehicleCategory;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface specifying vehicle catalog and fleet management contracts.
 * Demonstrates Abstraction in OOP architecture.
 */
public interface VehicleService {

    List<Vehicle> getAllVehicles();

    List<Vehicle> getAvailableVehicles();

    List<Vehicle> filterVehicles(VehicleCategory category, Double maxPrice, LocalDate pickupDate, LocalDate returnDate);

    Vehicle getVehicleById(Long id);

    boolean isVehicleAvailableForDates(Long vehicleId, LocalDate pickupDate, LocalDate returnDate);

    Vehicle createVehicle(VehicleDto vehicleDto);

    Vehicle updateVehicle(Long id, VehicleDto vehicleDto);

    Vehicle toggleVehicleAvailability(Long id);

    void deleteVehicle(Long id);

    long countTotalVehicles();

    long countAvailableVehicles();
}
