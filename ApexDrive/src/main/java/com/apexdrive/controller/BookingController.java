package com.apexdrive.controller;

import com.apexdrive.dto.*;
import com.apexdrive.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Public REST Controller handling customer booking requests, quotes, and lookups.
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Submits and confirms a new vehicle reservation.
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Calculates the projected rental cost and discounts for a prospective date range.
     */
    @PostMapping("/calculate-price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(@Valid @RequestBody PriceCalculationRequest request) {
        PriceCalculationResponse response = bookingService.calculatePrice(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Looks up an existing booking requiring matching reference AND email.
     * Protects customer privacy as specified in security guidelines.
     */
    @GetMapping("/lookup")
    public ResponseEntity<BookingResponse> lookupBooking(
            @RequestParam String reference,
            @RequestParam String email) {
        BookingResponse response = bookingService.getBookingForCustomer(reference, email);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all bookings associated with a verified customer email.
     */
    @GetMapping("/customer")
    public ResponseEntity<List<BookingResponse>> getCustomerBookings(@RequestParam String email) {
        List<BookingResponse> list = bookingService.getBookingsByCustomerEmail(email);
        return ResponseEntity.ok(list);
    }

    /**
     * Cancels an existing reservation upon email ownership verification.
     */
    @PostMapping("/{reference}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable String reference,
            @Valid @RequestBody CancelBookingRequest request) {
        BookingResponse response = bookingService.cancelBooking(reference, request.getCustomerEmail(), request.getReason());
        return ResponseEntity.ok(response);
    }
}
