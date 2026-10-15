package com.apexdrive.controller;

import com.apexdrive.dto.AdminStatsResponse;
import com.apexdrive.dto.BookingResponse;
import com.apexdrive.dto.VehicleDto;
import com.apexdrive.model.Vehicle;
import com.apexdrive.service.BookingService;
import com.apexdrive.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Protected Administrative Controller for fleet managers.
 * Requires admin token or key for all operational changes.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final VehicleService vehicleService;
    private final BookingService bookingService;

    @Value("${apexdrive.admin.username:admin}")
    private String adminUsername;

    @Value("${apexdrive.admin.password:ApexAdmin2026!}")
    private String adminPassword;

    public AdminController(VehicleService vehicleService, BookingService bookingService) {
        this.vehicleService = vehicleService;
        this.bookingService = bookingService;
    }

    /**
     * Authenticates an administrative session.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "token", "APEX_ADM_" + Integer.toHexString(adminPassword.hashCode()),
                    "message", "Admin authentication successful"
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "authenticated", false,
                "message", "Invalid administrative credentials."
        ));
    }

    private void verifyAdminAuth(String adminKey) {
        String expectedKey = "APEX_ADM_" + Integer.toHexString(adminPassword.hashCode());
        if (adminKey == null || (!adminKey.equals(expectedKey) && !adminKey.equals(adminPassword))) {
            throw new com.apexdrive.exception.UnauthorizedAccessException("Unauthorized administrative access. Valid Admin Token required.");
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        verifyAdminAuth(adminKey);
        return ResponseEntity.ok(bookingService.getAdminStats());
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        verifyAdminAuth(adminKey);
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping("/bookings/{reference}/cancel")
    public ResponseEntity<BookingResponse> adminCancelBooking(
            @PathVariable String reference,
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        verifyAdminAuth(adminKey);
        String reason = body != null ? body.get("reason") : "Administrative cancellation";
        return ResponseEntity.ok(bookingService.adminCancelBooking(reference, reason));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        verifyAdminAuth(adminKey);
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @PostMapping("/vehicles")
    public ResponseEntity<Vehicle> createVehicle(
            @Valid @RequestBody VehicleDto dto,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        verifyAdminAuth(adminKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(dto));
    }

    @PutMapping("/vehicles/{id}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDto dto,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        verifyAdminAuth(adminKey);
        return ResponseEntity.ok(vehicleService.updateVehicle(id, dto));
    }

    @PatchMapping("/vehicles/{id}/toggle-status")
    public ResponseEntity<Vehicle> toggleVehicleStatus(
            @PathVariable Long id,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        verifyAdminAuth(adminKey);
        return ResponseEntity.ok(vehicleService.toggleVehicleAvailability(id));
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Map<String, String>> deleteVehicle(
            @PathVariable Long id,
            @RequestHeader(value = "X-Admin-Key", required = false) String adminKey) {
        verifyAdminAuth(adminKey);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(Map.of("message", "Vehicle ID " + id + " deleted successfully."));
    }
}
