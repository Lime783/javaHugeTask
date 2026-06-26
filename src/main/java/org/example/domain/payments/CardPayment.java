package org.example.domain.payments;

import org.example.money.Money;

import java.util.Objects;
import java.util.regex.Pattern;

public class CardPayment extends Payment {
    private String last4Digits;
    private static final Pattern FOUR_DIGITS = Pattern.compile("^\\d{4}$");

    public CardPayment(Money amount, String paymentId, String last4Digits) {
        // TODO: regex dla paymentId
        Objects.requireNonNull(paymentId, "paymentId cannot be null");
        Objects.requireNonNull(last4Digits, "last4Digits cannot be null");
        if (!(FOUR_DIGITS.matcher(last4Digits).matches())) {
            throw new IllegalArgumentException("Invalid 4 digits: " + last4Digits);
        }
        this.amount = amount;
        this.paymentId = paymentId;
        this.paymentStatus = PaymentStatus.INITIATED;
        this.last4Digits = last4Digits;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public void setLast4Digits(String last4Digits) {
        this.last4Digits = last4Digits;
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
