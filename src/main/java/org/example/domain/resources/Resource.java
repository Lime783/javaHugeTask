package org.example.domain.resources;

import org.example.money.Money;


public abstract class Resource {
    protected String name;
    protected Money customHourlyRate;

    public String getName() {
        return name;
    }

    protected abstract Money baseRatePerHour();

    public abstract String describe();

    public Money hourlyRate() {
        return (customHourlyRate != null ? customHourlyRate : baseRatePerHour());
    }
}
