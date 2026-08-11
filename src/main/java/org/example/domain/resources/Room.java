package org.example.domain.resources;

import org.example.money.Money;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

public class Room extends Resource {

    public static final int MIN_DESC_LENGTH = 3;
    public static final int MAX_DESC_LENGTH = 100;
    private int seats;
    private Set<String> equipment;

    public Room(String name, Money customHourlyRate, int seats, Set<String> equipment) {
        Objects.requireNonNull(name, "Room name cannot be null");

        isDescValid(name);
        hasSeats(seats);

        this.name = name;
        this.customHourlyRate = customHourlyRate;
        this.seats = seats;
        this.equipment = equipment;
    }

    private static void isDescValid(String name) {
        if (name.length() < MIN_DESC_LENGTH || name.length() > MAX_DESC_LENGTH) {
            throw new IllegalArgumentException("Invalid room name: " + name);
        }
    }

    private static void hasSeats(int seats) {
        if (seats < 0) {
            throw new IllegalArgumentException("Number of seats cannot be negative: " + seats);
        }
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
