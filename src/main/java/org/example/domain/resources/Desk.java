package org.example.domain.resources;

import org.example.money.Money;

import java.math.BigDecimal;
import java.util.Objects;

public class Desk extends Resource {

    DeskType deskType;

    public Desk(String name, Money customHourlyRate, DeskType deskType) {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(deskType, "deskType cannot be null");
        if (name.length() < 3 || name.length() > 100) {
            throw new IllegalArgumentException("Invalid desk name: " + name);
        }
        if (NAMES_UNIQUE.contains(name)) {
            throw new IllegalArgumentException("Desk name already exists: " + name);
        }
        this.name = name;
        this.customHourlyRate = customHourlyRate;
        this.deskType = deskType;
    }

    public Desk(String name, DeskType deskType) {
        this(name, null, deskType);
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

    public DeskType getDeskType() {
        return deskType;
    }

    public void setDeskType(DeskType deskType) {
        this.deskType = deskType;
    }

    @Override
    protected Money baseRatePerHour() {
        return new Money(new BigDecimal("12.34"));
    }

    @Override
    public String describe() {
        return "Desk \"" + name + "\" which is " + deskType;
    }

    @Override
    public String toString() {
        return "Desk{" +
                "name='" + name + '\'' +
                ", deskType=" + deskType +
                ", customHourlyRate=" + customHourlyRate +
                '}';
    }
}
