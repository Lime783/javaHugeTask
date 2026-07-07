package org.example.services;

import org.example.domain.bookings.Booking;
import org.example.domain.payments.Billable;
import org.example.domain.payments.Invoice;

import java.time.LocalDateTime;

public class BillingService implements Billable {

    @Override
    public Invoice toInvoice(Booking booking) {
        return new Invoice(LocalDateTime.now(), booking.getUser(), booking.getCalculatedPrice(), "description of booking: " + booking.toString());
    }
}