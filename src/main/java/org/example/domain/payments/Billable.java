package org.example.domain.payments;

import org.example.domain.bookings.Booking;

public interface Billable {
    Invoice toInvoice(Booking booking);
}
