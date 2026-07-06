package org.example.repositories;

import org.example.domain.bookings.Booking;

import java.util.ArrayList;
import java.util.List;

public class InMemoryBookingRepository implements BookingRepository {
    private final List<Booking> bookings;

    public InMemoryBookingRepository() {
        bookings = new ArrayList<>();
    }

    public void add(Booking bookingToAdd) {
        bookings.add(bookingToAdd);
    }

    public Booking findBookingByID(String id) {
        for (Booking booking : bookings) {
            if (booking.getId().equals(id)) {
                return booking;
            }
        }
        throw new IllegalArgumentException("Booking with ID " + id + " not found");
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}
