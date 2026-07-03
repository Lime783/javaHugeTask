package org.example.pricing;

import org.example.domain.bookings.Booking;
import org.example.money.Money;

public interface PricingPolicy {
    Money calculatePrice(Booking booking);
}
