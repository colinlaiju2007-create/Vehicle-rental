package com.apexdrive.service;

import com.apexdrive.dto.*;
import com.apexdrive.exception.InvalidBookingException;
import com.apexdrive.exception.ResourceNotFoundException;
import com.apexdrive.exception.VehicleUnavailableException;
import com.apexdrive.model.*;
import com.apexdrive.repository.BookingRepository;
import com.apexdrive.repository.CustomerRepository;
import com.apexdrive.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of BookingService coordinating business rules, validation,
 * concurrency-safe availability validation, and persistence.
 */
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final SecureRandom random = new SecureRandom();

    public BookingServiceImpl(BookingRepository bookingRepository,
                              VehicleRepository vehicleRepository,
                              CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        // 1. Backend Date Validation
        validateDates(request.getPickupDate(), request.getReturnDate());

        // 2. Fetch Vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + request.getVehicleId() + " not found."));

        if (!Boolean.TRUE.equals(vehicle.getAvailable())) {
            throw new VehicleUnavailableException("Vehicle '" + vehicle.getName() + "' is currently decommissioned or undergoing maintenance.");
        }

        // 3. Check for Overlapping Reservations
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                vehicle.getId(), request.getPickupDate(), request.getReturnDate()
        );
        if (!overlapping.isEmpty()) {
            throw new VehicleUnavailableException(
                    "Vehicle '" + vehicle.getName() + "' is already reserved for the chosen date range (" +
                    request.getPickupDate() + " to " + request.getReturnDate() + "). Please select alternate dates or another vehicle."
            );
        }

        // 4. Find or Create Customer
        String sanitizedEmail = request.getCustomerEmail().trim().toLowerCase(Locale.ROOT);
        Customer customer = customerRepository.findByEmailIgnoreCase(sanitizedEmail)
                .map(existing -> {
                    existing.setFullName(request.getCustomerName().trim());
                    existing.setPhone(request.getCustomerPhone().trim());
                    if (request.getDriverLicense() != null && !request.getDriverLicense().isBlank()) {
                        existing.setDriverLicense(request.getDriverLicense().trim());
                    }
                    return existing;
                })
                .orElseGet(() -> new Customer(
                        request.getCustomerName().trim(),
                        sanitizedEmail,
                        request.getCustomerPhone().trim(),
                        request.getDriverLicense()
                ));
        customer = customerRepository.save(customer);

        // 5. Calculate Rental Cost polymorphically using OOP
        long days = Booking.calculateDurationDays(request.getPickupDate(), request.getReturnDate());
        double totalCost = vehicle.calculateRentalCost(days);

        // 6. Generate Unique Booking Reference
        String bookingRef = generateUniqueReference();

        // 7. Construct and Persist Booking
        Booking booking = new Booking(
                bookingRef,
                customer,
                vehicle,
                request.getPickupDate(),
                request.getReturnDate(),
                totalCost,
                request.getSpecialRequests()
        );

        Booking savedBooking = bookingRepository.save(booking);
        return BookingResponse.fromEntity(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        validateDates(request.getPickupDate(), request.getReturnDate());

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));

        long days = Booking.calculateDurationDays(request.getPickupDate(), request.getReturnDate());
        double baseRate = vehicle.getDailyPrice() * days;
        double finalTotal = vehicle.calculateRentalCost(days);
        double discountOrAdj = Math.round((finalTotal - baseRate) * 100.0) / 100.0;

        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                vehicle.getId(), request.getPickupDate(), request.getReturnDate()
        );
        boolean availableForDates = overlapping.isEmpty() && Boolean.TRUE.equals(vehicle.getAvailable());

        return new PriceCalculationResponse(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getDailyPrice(),
                days,
                Math.round(baseRate * 100.0) / 100.0,
                finalTotal,
                discountOrAdj,
                vehicle.getSpecialPerks(),
                availableForDates
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingByReference(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResourceNotFoundException("Booking reference '" + bookingReference + "' was not found."));
        return BookingResponse.fromEntity(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingForCustomer(String bookingReference, String customerEmail) {
        String ref = bookingReference.trim().toUpperCase(Locale.ROOT);
        String email = customerEmail.trim().toLowerCase(Locale.ROOT);

        Booking booking = bookingRepository.findByBookingReferenceAndCustomerEmailIgnoreCase(ref, email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No reservation matching reference '" + ref + "' and email '" + email + "' was found."
                ));
        return BookingResponse.fromEntity(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomerEmail(String customerEmail) {
        String email = customerEmail.trim().toLowerCase(Locale.ROOT);
        return bookingRepository.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(email)
                .stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(BookingResponse::fromEntity)
                .toList();
    }

    @Override
    public BookingResponse cancelBooking(String bookingReference, String customerEmail, String reason) {
        String ref = bookingReference.trim().toUpperCase(Locale.ROOT);
        String email = customerEmail.trim().toLowerCase(Locale.ROOT);

        Booking booking = bookingRepository.findByBookingReferenceAndCustomerEmailIgnoreCase(ref, email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authorization failed: Booking reference and customer email do not match."
                ));

        booking.cancelBooking(reason);
        Booking updated = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updated);
    }

    @Override
    public BookingResponse adminCancelBooking(String bookingReference, String reason) {
        String ref = bookingReference.trim().toUpperCase(Locale.ROOT);
        Booking booking = bookingRepository.findByBookingReference(ref)
                .orElseThrow(() -> new ResourceNotFoundException("Booking reference '" + ref + "' not found."));

        booking.cancelBooking(reason != null ? reason : "Administrative cancellation");
        Booking updated = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse getAdminStats() {
        long totalVehicles = vehicleRepository.count();
        long availableVehicles = vehicleRepository.findByAvailableTrue().size();
        long totalBookings = bookingRepository.count();
        long cancelledBookings = bookingRepository.countByStatus(BookingStatus.CANCELLED);
        long activeBookings = totalBookings - cancelledBookings;
        Double revenue = bookingRepository.calculateGrossRevenue();

        return new AdminStatsResponse(
                totalVehicles,
                availableVehicles,
                totalBookings,
                activeBookings,
                cancelledBookings,
                revenue != null ? revenue : 0.0
        );
    }

    // --- Private Helper Methods ---

    private void validateDates(LocalDate pickupDate, LocalDate returnDate) {
        if (pickupDate == null || returnDate == null) {
            throw new InvalidBookingException("Both pickup date and return date are required.");
        }
        LocalDate today = LocalDate.now();
        if (pickupDate.isBefore(today)) {
            throw new InvalidBookingException("Pickup date cannot be in the past. (Selected: " + pickupDate + ", Current: " + today + ")");
        }
        if (returnDate.isBefore(pickupDate)) {
            throw new InvalidBookingException("Return date cannot be earlier than pickup date.");
        }
    }

    private String generateUniqueReference() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int attempts = 0; attempts < 100; attempts++) {
            StringBuilder sb = new StringBuilder("APX-");
            for (int i = 0; i < 4; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            sb.append("-");
            for (int i = 0; i < 4; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            String candidate = sb.toString();
            if (bookingRepository.findByBookingReference(candidate).isEmpty()) {
                return candidate;
            }
        }
        return "APX-" + System.currentTimeMillis();
    }
}
