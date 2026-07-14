package org.example.services;

import org.example.domain.bookings.Booking;
import org.example.domain.bookings.BookingStatus;
import org.example.domain.payments.CardPayment;
import org.example.domain.payments.Payment;
import org.example.repositories.InMemoryBookingRepository;

import java.util.Objects;

public class PaymentService {

    private final InMemoryBookingRepository inMemoryBookingRepository;

    public PaymentService(InMemoryBookingRepository inMemoryBookingRepository) {
        this.inMemoryBookingRepository = inMemoryBookingRepository;
    }

    public Payment pay(String bookingId, String cardLast4) {
        Booking bookingToPayFor = inMemoryBookingRepository.findBookingByID(bookingId);
        Objects.requireNonNull(bookingToPayFor.getCalculatedPrice(), "Price wasn't calculated yet");
        if (!(Objects.equals(bookingToPayFor.getBookingStatus(), BookingStatus.PENDING))){
            throw new IllegalStateException("Booking status was not PENDING, it's already " + bookingToPayFor.getBookingStatus());
        }

        CardPayment cardPayment = new CardPayment(bookingToPayFor.getCalculatedPrice(), cardLast4);
        cardPayment.capture();
        bookingToPayFor.setPayment(cardPayment);
        return cardPayment;
    }
}
