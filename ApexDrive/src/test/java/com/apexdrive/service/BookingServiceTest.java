package com.apexdrive.service;

import com.apexdrive.dto.BookingRequest;
import com.apexdrive.dto.BookingResponse;
import com.apexdrive.exception.InvalidBookingException;
import com.apexdrive.exception.ResourceNotFoundException;
import com.apexdrive.exception.VehicleUnavailableException;
import com.apexdrive.model.*;
import com.apexdrive.repository.BookingRepository;
import com.apexdrive.repository.CustomerRepository;
import com.apexdrive.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Vehicle sampleVehicle;

    @BeforeEach
    void setUp() {
        sampleVehicle = new Vehicle("BMW M4 Competition", VehicleCategory.SPORTS, 300.0,
                "Twin Turbo", "Auto", 4, "Petrol", "bmw.jpg", true, 300.0);
        sampleVehicle.setId(1L);
    }

    @Test
    @DisplayName("Validation: Reject pickup date in the past")
    void testPastPickupDateRejected() {
        BookingRequest request = new BookingRequest(1L, "John Doe", "john@example.com",
                "+1234567890", "DL12345",
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), "None");

        assertThrows(InvalidBookingException.class, () -> bookingService.createBooking(request));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Validation: Reject return date before pickup date")
    void testReturnDateBeforePickupRejected() {
        BookingRequest request = new BookingRequest(1L, "John Doe", "john@example.com",
                "+1234567890", "DL12345",
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(2), "None");

        assertThrows(InvalidBookingException.class, () -> bookingService.createBooking(request));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Availability: Reject overlapping active bookings")
    void testOverlappingBookingRejected() {
        LocalDate pickup = LocalDate.now().plusDays(2);
        LocalDate returnD = LocalDate.now().plusDays(5);

        BookingRequest request = new BookingRequest(1L, "John Doe", "john@example.com",
                "+1234567890", "DL12345", pickup, returnD, "None");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));

        // Simulate an existing overlapping reservation
        Booking existing = new Booking();
        existing.setId(99L);
        when(bookingRepository.findOverlappingBookings(1L, pickup, returnD))
                .thenReturn(List.of(existing));

        VehicleUnavailableException ex = assertThrows(VehicleUnavailableException.class,
                () -> bookingService.createBooking(request));
        assertTrue(ex.getMessage().contains("already reserved"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Booking Creation: Successfully reserve available vehicle and calculate price")
    void testSuccessfulBookingCreation() {
        LocalDate pickup = LocalDate.now().plusDays(1);
        LocalDate returnD = LocalDate.now().plusDays(3); // 2 days

        BookingRequest request = new BookingRequest(1L, "Alice Johnson", "alice@apexdrive.com",
                "+19876543210", "DL9988", pickup, returnD, "VIP Arrival");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(bookingRepository.findOverlappingBookings(1L, pickup, returnD)).thenReturn(List.of());

        Customer customer = new Customer("Alice Johnson", "alice@apexdrive.com", "+19876543210", "DL9988");
        customer.setId(10L);
        when(customerRepository.findByEmailIgnoreCase("alice@apexdrive.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        when(bookingRepository.findByBookingReference(anyString())).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(50L);
            return b;
        });

        BookingResponse response = bookingService.createBooking(request);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertNotNull(response.getBookingReference());
        assertTrue(response.getBookingReference().startsWith("APX-"));
        assertEquals("Alice Johnson", response.getCustomerName());
        assertEquals("alice@apexdrive.com", response.getCustomerEmail());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals(600.0, response.getTotalRentalCost()); // 2 days * 300 = 600.0
    }

    @Test
    @DisplayName("Cancellation: Cancel booking updates status to CANCELLED and preserves record")
    void testCancelBooking() {
        Customer customer = new Customer("Alice Johnson", "alice@apexdrive.com", "+19876543210", "DL9988");
        Booking booking = new Booking("APX-TEST-001", customer, sampleVehicle,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(4), 600.0, "None");

        when(bookingRepository.findByBookingReferenceAndCustomerEmailIgnoreCase("APX-TEST-001", "alice@apexdrive.com"))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse cancelled = bookingService.cancelBooking("APX-TEST-001", "alice@apexdrive.com", "Travel rescheduled");

        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());
        assertEquals("Travel rescheduled", cancelled.getCancellationReason());
        assertNotNull(cancelled.getCancelledAt());
    }

    @Test
    @DisplayName("Security & Privacy: Unauthorized customer cannot lookup another customer's booking")
    void testUnauthorizedCustomerLookupFails() {
        when(bookingRepository.findByBookingReferenceAndCustomerEmailIgnoreCase("APX-TEST-001", "hacker@evil.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getBookingForCustomer("APX-TEST-001", "hacker@evil.com"));
    }
}
