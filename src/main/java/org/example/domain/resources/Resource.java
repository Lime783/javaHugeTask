package org.example.domain.resources;

import org.example.money.Money;

import java.util.HashSet;

abstract class Resource {
    protected String name;
    protected Money customHourlyRate;
    final static protected HashSet<String> NAMES_UNIQUE = new HashSet<>();

    protected abstract Money baseRatePerHour();

    public abstract String describe();

    public Money hourlyRate() {
        return (customHourlyRate != null ? customHourlyRate : baseRatePerHour());
    }
}
