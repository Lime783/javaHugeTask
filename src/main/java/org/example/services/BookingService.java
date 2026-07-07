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
import java.util.List;
import java.util.Objects;

public class BookingService {
    public Booking book(User user, Resource resource, LocalDateTime startTime, LocalDateTime endTime, Payment paymentMethod, InMemoryBookingRepository inMemoryBookingRepository) {
        Objects.requireNonNull(user, "User must not be null");
        Objects.requireNonNull(resource, "Resource must not be null");
        Objects.requireNonNull(startTime, "Start date must not be null");
        Objects.requireNonNull(endTime, "End date must not be null");

        // TODO: zmienic id i payment, ew. odwołanie do bazy danych
        Booking newBooking = new Booking("123", user, resource, startTime, endTime, paymentMethod);
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

    public Booking book(User user, Resource resource, LocalDateTime startTime, LocalDateTime endTime, InMemoryBookingRepository inMemoryBookingRepository) {
        return book(user, resource, startTime, endTime, null, inMemoryBookingRepository);
    }

    public Booking book(User user, Resource resource, LocalDateTime startTime, int durationInMinutes, Payment paymentMethod, InMemoryBookingRepository inMemoryBookingRepository) {
        LocalDateTime endTime = startTime.plusMinutes(durationInMinutes);
        return book(user, resource, startTime, endTime, paymentMethod, inMemoryBookingRepository);
    }

    public Booking book(User user, Resource resource, LocalDateTime startTime, int durationInMinutes, InMemoryBookingRepository inMemoryBookingRepository) {
        LocalDateTime endTime = startTime.plusMinutes(durationInMinutes);
        return book(user, resource, startTime, endTime, null, inMemoryBookingRepository);
    }

    public void confirmBooking(Booking bookingToConfirm) {
        if (!(bookingToConfirm.getBookingStatus().equals(BookingStatus.PENDING))) {
            throw new IllegalStateException("booking: " + bookingToConfirm.getId() + " cannot be confirmed, must be pending");
        }
        bookingToConfirm.setBookingStatus(BookingStatus.CONFIRMED);
    }

    public void cancelBooking(Booking bookingToCancel) {
        BookingStatus bookingStatus = bookingToCancel.getBookingStatus();
        if (bookingStatus.equals(BookingStatus.PENDING) || bookingStatus.equals(BookingStatus.CONFIRMED)) {
            bookingToCancel.setBookingStatus(BookingStatus.CANCELLED);
        }
        throw new IllegalStateException("booking: " + bookingToCancel.getId() + " cannot be cancelled, must be pending or confirmed");
    }

    public void completeBooking(Booking bookingToComplete) {
        if (!(bookingToComplete.getBookingStatus().equals(BookingStatus.CONFIRMED))) {
            throw new IllegalStateException("booking: " + bookingToComplete.getId() + " cannot be completed, must be confirmed");
        }
        bookingToComplete.setBookingStatus(BookingStatus.COMPLETED);
    }

    //TODO: czy o to chodzi, dodac filtry
    public List<Booking> getBookings(InMemoryBookingRepository inMemoryBookingRepository) {
        return inMemoryBookingRepository.getBookings();
    }
}
