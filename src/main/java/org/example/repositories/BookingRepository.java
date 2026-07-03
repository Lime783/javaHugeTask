package org.example.repositories;

import org.example.domain.bookings.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    void add(Booking booking);
    Optional<Booking> findBookingByID(String id);
    List<Booking> findAllBookings();
}
