package org.example.repositories;

import org.example.domain.bookings.Booking;

import java.util.List;

public interface BookingRepository {
    void add(Booking bookingToAdd);

    Booking findBookingByID(String id);

    List<Booking> getBookings();
}
