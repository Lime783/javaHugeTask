package org.example.domain.resources;

import org.example.money.Money;

import java.math.BigDecimal;
import java.util.Objects;

public class Device extends Resource {

    int quantity;

    public Device(String name, Money customHourlyRate, int quantity) {
        if (name.length() < 3 || name.length() > 100) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Invalid quantity: " + quantity);
        }
        this.name = Objects.requireNonNull(name);
        this.customHourlyRate = customHourlyRate;
        this.quantity = quantity;
    }

    public Device(String name, int quantity) {
        this(name, null, quantity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Money getCustomHourlyRate() {
        return customHourlyRate;
    }

    public void setCustomHourlyRate(Money customHourlyRate) {
        this.customHourlyRate = customHourlyRate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    protected Money baseRatePerHour() {
        return new Money(new BigDecimal("0.01"));
    }

    @Override
    public String describe() {
        return "Device " + name + ", max to lend: " + quantity;
    }
}