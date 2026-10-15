package com.apexdrive.controller;

import com.apexdrive.model.Vehicle;
import com.apexdrive.model.VehicleCategory;
import com.apexdrive.service.VehicleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Public REST Controller exposing vehicle discovery and catalog filtering endpoints.
 */
@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Retrieves all available vehicles or applies category, price, and date filters.
     */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getVehicles(
            @RequestParam(required = false) VehicleCategory category,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        
        List<Vehicle> list = vehicleService.filterVehicles(category, maxPrice, pickupDate, returnDate);
        return ResponseEntity.ok(list);
    }

    /**
     * Retrieves vehicle details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    /**
     * Checks if a vehicle is available for a specified date range.
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        
        boolean isAvailable = vehicleService.isVehicleAvailableForDates(id, pickupDate, returnDate);
        return ResponseEntity.ok(Map.of(
                "vehicleId", id,
                "pickupDate", pickupDate,
                "returnDate", returnDate,
                "available", isAvailable
        ));
    }
}
