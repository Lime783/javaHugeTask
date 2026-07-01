package org.example.repositories;

import org.example.domain.bookings.Booking;

import java.util.List;

public class InMemoryBookingRepository implements BookingRepository {
    List<Booking> bookings;

    public void add(Booking bookingToAdd) {
        checkIfBookingIsValid(bookingToAdd);
        bookings.add(bookingToAdd);
    }

    private void checkIfBookingIsValid(Booking bookingToCheck) {
        for (Booking bookingInDataBase : bookings) {
            boolean bookingForResourceAlreadyExists = bookingToCheck.getResource().getName().equals(bookingInDataBase.getResource().getName());
            if (bookingForResourceAlreadyExists) {
                if (checkIfTimeOfBookingsCollide(bookingToCheck, bookingInDataBase)) {
                    throw new IllegalStateException("booking: " + bookingToCheck.getId() + " collides with booking: " + bookingInDataBase.getId());
                }
            }
        }
    }

    private boolean checkIfTimeOfBookingsCollide(Booking bookingToCheck, Booking bookingInDataBase) {
        return bookingToCheck.getStartTime().isBefore(bookingInDataBase.getEndTime()) &&
                bookingToCheck.getEndTime().isAfter(bookingInDataBase.getStartTime());
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
