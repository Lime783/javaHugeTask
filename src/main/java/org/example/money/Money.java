package org.example.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) {
    public Money(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative: " + amount);
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Money(String amount){
        this(new BigDecimal(amount));
    }

    static Money of(String amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative: " + amount);
        }
        return new Money(new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP));
    }

    public Money add(Money amountToAdd) {
        Objects.requireNonNull(amountToAdd, "amountToAdd must not be null");
        if (amountToAdd.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative: " + amountToAdd);
        }
        return new Money(amount.add(amountToAdd.amount));
    }

    public Money subtract(Money amountToSubtract) {
        Objects.requireNonNull(amountToSubtract, "amountToSubtract must not be null");
        if (amountToSubtract.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative: " + amountToSubtract);
        }
        if (amount.subtract(amountToSubtract.amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount to substract: " + amountToSubtract + " cannot be greater than current balance: " + amount + " PLN");
        }
        return new Money(amount.subtract(amountToSubtract.amount));
    }

    Money multiply(Money multiplier) {
        Objects.requireNonNull(multiplier, "multiplier must not be null");
        if (multiplier.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("multiplier cannot be negative: " + multiplier);
        }
        return new Money(amount.multiply(multiplier.amount).setScale(2, RoundingMode.HALF_UP));
    }

    int compareTo(Money moneyToCompare) {
        return amount.compareTo(moneyToCompare.amount);
    }

    @Override
    public String toString() {
        return amount + " PLN";
    }
}
