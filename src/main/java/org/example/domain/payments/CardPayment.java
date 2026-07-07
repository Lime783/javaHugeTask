package org.example.domain.payments;

import org.example.money.Money;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardPayment extends Payment {
    private String last4Digits;
    private static final Pattern FOUR_DIGITS = Pattern.compile("^\\d{4}$");
    private static final Pattern CODE_PATTERN = Pattern.compile("^PI-(\\d{8})-(\\d{1,3})$");

    private static int counter = 0;

    public CardPayment(Money amount, String last4Digits) {
        Objects.requireNonNull(last4Digits, "last4Digits cannot be null");
        if (!(FOUR_DIGITS.matcher(last4Digits).matches())) {
            throw new IllegalArgumentException("Invalid 4 digits: " + last4Digits);
        }

        String paymentId = "PI-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + counter++;
        isValidBookingId(paymentId);

        this.amount = amount;
        this.paymentId = paymentId;
        this.paymentStatus = PaymentStatus.INITIATED;
        this.last4Digits = last4Digits;
    }

    private void isValidBookingId(String id) {
        Matcher matcher = CODE_PATTERN.matcher(id);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid booking id: " + id);
        }

        String datePart = matcher.group(1);

        try {
            LocalDate.parse(datePart, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in booking id: " + datePart);
        }
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public void capture() {
        if (getPaymentStatus().equals(PaymentStatus.CAPTURED)) {
            throw  new IllegalStateException("Payment already captured");
        }
        paymentStatus = PaymentStatus.CAPTURED;
    }

    @Override
    public String toString() {
        return "CardPayment{" +
                "last4Digits='" + last4Digits + '\'' +
                ", amount=" + amount +
                ", paymentId='" + paymentId + '\'' +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
