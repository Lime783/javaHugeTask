package org.example.domain.resources;

import org.example.money.Money;

import java.math.BigDecimal;
import java.util.Objects;

public class Device extends Resource {

    int maxQuantity;
    int remainingQuantity;

    public Device(String name, Money customHourlyRate, int maxQuantity) {
        if (name.length() < 3 || name.length() > 100) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        if (maxQuantity < 1) {
            throw new IllegalArgumentException("Invalid quantity: " + maxQuantity);
        }
        if (NAMES_UNIQUE.contains(name)) {
            throw new IllegalArgumentException("Device name already exists: " + name);
        }
        this.name = Objects.requireNonNull(name);
        this.customHourlyRate = customHourlyRate;
        this.maxQuantity = maxQuantity;
        this.remainingQuantity = maxQuantity;
        NAMES_UNIQUE.add(name);
    }

    public Device(String name, int maxQuantity) {
        this(name, null, maxQuantity);
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    @Override
    protected Money baseRatePerHour() {
        return new Money(new BigDecimal("0.01"));
    }

    @Override
    public String describe() {
        return "Device " + name + ", max to lend: " + maxQuantity;
    }
}