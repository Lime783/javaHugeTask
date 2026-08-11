package org.example.pricing;

import org.example.domain.bookings.Booking;
import org.example.money.Money;

import java.math.BigDecimal;

public class HappyHoursPricing implements PricingPolicy {
    private final static String MINUTES_IN_HOUR = "60";
    private final static BigDecimal discountPercentage = new BigDecimal("0.30");

    @Override
    public Money calculatePrice(Booking booking) {
        BigDecimal durationOfBookingInMinutes = new BigDecimal(booking.durationInMinutes());

        return booking.getResource().hourlyRate()
                .multiply(durationOfBookingInMinutes)
                .multiply(BigDecimal.ONE.subtract(discountPercentage))
                .divide(MINUTES_IN_HOUR);
    }
}