package org.example.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) {
    public Money(BigDecimal amount) {
        Objects.requireNonNull(amount);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative: " + amount);
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    static Money of(String amount) {
        Objects.requireNonNull(amount);
        if (new BigDecimal(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative: " + amount);
        }
        return new Money(new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP));
    }

    Money add(BigDecimal amountToAdd) {
        if (amountToAdd.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative: " + amountToAdd);
        }
        return new Money(amount.add(amountToAdd));
    }
    
    Money subtract(BigDecimal amountToSubtract) {
        if (amountToSubtract.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative: " + amountToSubtract);
        }
        if (amount.subtract(amountToSubtract).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount to substract cannot be greater than current balance: " + amountToSubtract);
        }
        return new Money(amount.subtract(amountToSubtract));
    }
    
    Money multiply(BigDecimal multiplier) {
        if (multiplier.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("multiplier cannot be negative: " + multiplier);
        }
        return new Money(amount.multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
    }

    int compareTo(Money moneyToCompare) {
        return amount.compareTo(moneyToCompare.amount);
    }

    @Override
    public String toString() {
        return amount + " PLN";
    }
}
