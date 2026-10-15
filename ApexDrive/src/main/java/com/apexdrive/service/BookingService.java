package com.apexdrive.service;

import com.apexdrive.dto.*;

import java.util.List;

/**
 * Service interface specifying business workflows for reservation lifecycles.
 * Demonstrates Abstraction and Separation of Concerns in OOP architecture.
 */
public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    PriceCalculationResponse calculatePrice(PriceCalculationRequest request);

    BookingResponse getBookingByReference(String bookingReference);

    BookingResponse getBookingForCustomer(String bookingReference, String customerEmail);

    List<BookingResponse> getBookingsByCustomerEmail(String customerEmail);

    List<BookingResponse> getAllBookings();

    BookingResponse cancelBooking(String bookingReference, String customerEmail, String reason);

    BookingResponse adminCancelBooking(String bookingReference, String reason);

    AdminStatsResponse getAdminStats();
}
