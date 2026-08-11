package org.example.pricing;

import org.example.domain.bookings.Booking;
import org.example.money.Money;

import java.math.BigDecimal;

public class StandardPricing implements PricingPolicy {
    private final static String MINUTES_IN_HOUR = "60";

    @Override
    public Money calculatePrice(Booking booking) {
        BigDecimal durationOfBookingInMinutes = new BigDecimal(booking.durationInMinutes());

        return booking.getResource().hourlyRate()
                .multiply(durationOfBookingInMinutes)
                .divide(MINUTES_IN_HOUR);
    }
}
