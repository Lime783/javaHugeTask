package org.example.services;

import org.example.domain.bookings.Booking;
import org.example.domain.bookings.BookingStatus;
import org.example.domain.payments.Payment;
import org.example.domain.resources.Desk;
import org.example.domain.resources.Device;
import org.example.domain.resources.Resource;
import org.example.domain.resources.Room;
import org.example.domain.users.User;
import org.example.pricing.HappyHoursPricing;
import org.example.pricing.PricingPolicy;
import org.example.pricing.StandardPricing;
import org.example.repositories.InMemoryBookingRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class BookingService {
    private static int counter = 0;
    private final InMemoryBookingRepository inMemoryBookingRepository;

    public BookingService(InMemoryBookingRepository inMemoryBookingRepository){
        this.inMemoryBookingRepository = inMemoryBookingRepository;
    }

    public Booking book(User user, Resource resource, LocalDateTime startTime, LocalDateTime endTime, Payment paymentMethod) {
        Objects.requireNonNull(user, "User must not be null");
        Objects.requireNonNull(resource, "Resource must not be null");
        Objects.requireNonNull(startTime, "Start date must not be null");
        Objects.requireNonNull(endTime, "End date must not be null");

        String id = "BK-" + startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + counter++;

        Booking newBooking = new Booking(id, user, resource, startTime, endTime, paymentMethod);
        checkIfBookingIsValid(newBooking, inMemoryBookingRepository);
        if (resource instanceof Device device) {
            device.setRemainingQuantity(device.getRemainingQuantity() - 1);
        }

        PricingPolicy pricingPolicy = determinePricingPolicy(newBooking);
        newBooking.setCalculatedPrice(pricingPolicy.calculatePrice(newBooking));

        inMemoryBookingRepository.add(newBooking);
        return newBooking;
    }

    private void checkIfBookingIsValid(Booking bookingToCheck, InMemoryBookingRepository inMemoryBookingRepository) {
        if (!(bookingToCheck.getStartTime().toLocalDate().equals(bookingToCheck.getEndTime().toLocalDate()))) {
            throw new IllegalArgumentException("One booking for one day, start: " + bookingToCheck.getStartTime() + " end: " + bookingToCheck.getEndTime());
        }

        if (bookingToCheck.getStartTime().toLocalTime().isBefore(LocalTime.of(8, 0)) ||
                bookingToCheck.getEndTime().toLocalTime().isAfter(LocalTime.of(20, 0))) {
            throw new IllegalArgumentException("Cannot make a booking starting before 8:00 or ending after 20:00");
        }

        for (Booking bookingInDataBase : inMemoryBookingRepository.getBookings()) {
            boolean bookingForResourceAlreadyExists = bookingToCheck.getResource().getName().equals(bookingInDataBase.getResource().getName());
            if (bookingForResourceAlreadyExists) {
                validateParticularResource(bookingToCheck, bookingInDataBase);
            }
        }
    }

    private void validateParticularResource(Booking bookingToCheck, Booking bookingInDataBase) {
        Resource resource = bookingToCheck.getResource();
        if (resource instanceof Room || resource instanceof Desk) {
            if (isAnyOfBookingsPendingOrConfirmed(bookingToCheck, bookingInDataBase) && timeOfBookingsCollide(bookingToCheck, bookingInDataBase)) {
                throw new IllegalStateException("booking: " + bookingToCheck.getId() + " " + bookingToCheck.getStartTime() + " to " + bookingToCheck.getEndTime()
                        + " collides with booking: " + bookingInDataBase.getId() + " " + bookingInDataBase.getStartTime() + " to " + bookingInDataBase.getEndTime());
            }
        } else if (resource instanceof Device device) {
            if (device.getRemainingQuantity() == 0) {
                throw new IllegalStateException("There are no more " + device.getName() + "s left to lend at the moemnt");
            }
        }
    }

    private boolean isAnyOfBookingsPendingOrConfirmed(Booking bookingToCheck, Booking bookingInDataBase) {
        return bookingToCheck.getBookingStatus().equals(BookingStatus.PENDING)
                || bookingToCheck.getBookingStatus().equals(BookingStatus.CONFIRMED)
                || bookingInDataBase.getBookingStatus().equals(BookingStatus.PENDING)
                || bookingInDataBase.getBookingStatus().equals(BookingStatus.CONFIRMED);
    }

    private boolean timeOfBookingsCollide(Booking bookingToCheck, Booking bookingInDataBase) {
        return bookingToCheck.getStartTime().isBefore(bookingInDataBase.getEndTime()) &&
                bookingToCheck.getEndTime().isAfter(bookingInDataBase.getStartTime());
    }

    private PricingPolicy determinePricingPolicy(Booking booking) {
        if (booking.getStartTime().toLocalTime().isBefore(LocalTime.of(16, 0))) {
            return new HappyHoursPricing();
        } else {
            return new StandardPricing();
        }
    }

    public Booking book(User user, Resource resource, LocalDateTime startTime, LocalDateTime endTime) {
        return book(user, resource, startTime, endTime, null);
    }

    public void confirmBooking(Booking bookingToConfirm) {
        bookingToConfirm.confirmBooking();
    }

    public void cancelBooking(Booking bookingToCancel) {
        bookingToCancel.cancelBooking();
    }

    public void completeBooking(Booking bookingToComplete) {
        bookingToComplete.completeBooking();
    }

    public List<Booking> getBookings(InMemoryBookingRepository inMemoryBookingRepository) {
        return inMemoryBookingRepository.getBookings();
    }
}
