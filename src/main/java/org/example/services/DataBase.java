package org.example.services;

import org.example.domain.bookings.Booking;
import org.example.domain.resources.Resource;
import org.example.domain.users.User;
import org.example.repositories.BookingRepository;
import org.example.repositories.ResourceRepository;
import org.example.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public class DataBase implements BookingRepository, ResourceRepository, UserRepository {

    private static List<Booking> bookings;
    private static List<Resource> resources;
    private static List<User> users;

    @Override
    public void add(Booking bookingToAdd) {
        checkIfBookingIsValid(bookingToAdd);
        bookings.add(bookingToAdd);
    }

    private void checkIfBookingIsValid(Booking bookingToCheck) {
        for (Booking bookingInDataBase : bookings) {
            boolean bookingForResourceAlreadyExists = bookingToCheck.getResource().getName().equals(bookingInDataBase.getResource().getName());
            if (bookingForResourceAlreadyExists) {
                if (checkIfTimeOfBookingsCollide(bookingToCheck, bookingInDataBase)) {
                    throw new IllegalStateException("booking: " + bookingToCheck.getId() + " collides with booking: " + bookingInDataBase.getId());
                }
            }
        }
    }

    private boolean checkIfTimeOfBookingsCollide(Booking bookingToCheck, Booking bookingInDataBase) {
        return (bookingToCheck.getStartTime().isAfter(bookingInDataBase.getStartTime())
                && bookingToCheck.getStartTime().isBefore(bookingInDataBase.getEndTime()))
                ||
                (bookingToCheck.getEndTime().isAfter(bookingInDataBase.getStartTime())
                && bookingToCheck.getEndTime().isBefore(bookingInDataBase.getEndTime()));
    }

    @Override
    public Optional<Booking> findBookingByID(String id) {
        return Optional.empty();
    }

    @Override
    public List<Booking> findAllBookings() {
        return bookings;
    }

    @Override
    public void addResource(Resource resource) {
        resources.add(resource);
    }

    @Override
    public Optional<Resource> findResourceByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<Resource> findAllResources() {
        return resources;
    }

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<User> findAllUsers() {
        return users;
    }
}
