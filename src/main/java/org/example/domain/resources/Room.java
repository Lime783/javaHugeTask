package org.example.domain.resources;

import org.example.money.Money;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

public class Room extends Resource {

    private int seats;
    private Set<String> equipment;

    public Room(String name, Money customHourlyRate, int seats, Set<String> equipment) {
        Objects.requireNonNull(name, "Room name cannot be null");
        if (name.length() < 3 || name.length() > 100) {
            throw new IllegalArgumentException("Invalid room name: " + name);
        }
        if (seats < 0) {
            throw new IllegalArgumentException("Number of seats cannot be negative: " + seats);
        }
        if (NAMES_UNIQUE.contains(name)) {
            throw new IllegalArgumentException("Room name already exists: " + name);
        }
        this.name = name;
        this.customHourlyRate = customHourlyRate;
        this.seats = seats;
        this.equipment = equipment;
        NAMES_UNIQUE.add(name);
    }

    public Room(String name, int seats, Set<String> equipment) {
        this(name, null, seats, equipment);
    }

    public Room(String name, Money customHourlyRate, int seats) {
        this(name, customHourlyRate, seats, Set.of());
    }

    public Room(String name, int seats) {
        this(name, null, seats, Set.of());
    }

    @Override
    protected Money baseRatePerHour() {
        return new Money(new BigDecimal("60.00"));
    }

    @Override
    public String describe() {
        if (equipment.isEmpty()) {
            return "Room " + name + " without equipment";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Room ").append(name).append(" with: ");
        for (String equipmentName : equipment) {
            stringBuilder.append(equipmentName).append(", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", seats=" + seats +
                ", equipment=" + equipment +
                ", customHourlyRate=" + customHourlyRate +
                '}';
    }
}
