package org.example.services;

import org.example.domain.bookings.Booking;
import org.example.domain.payments.CardPayment;
import org.example.domain.payments.Payment;
import org.example.repositories.InMemoryBookingRepository;

public class PaymentService {
    public Payment pay(String bookingId, String cardLast4, InMemoryBookingRepository inMemoryBookingRepository) {
        Booking bookingToPayFor = inMemoryBookingRepository.findBookingByID(bookingId);

        // TODO: zmienic paymentId
        CardPayment cardPayment = new CardPayment(bookingToPayFor.getCalculatedPrice(), "123", cardLast4);
        cardPayment.capture();
        bookingToPayFor.setPayment(cardPayment);
        return cardPayment;
    }
}
