package org.example.domain.bookings;

import org.example.domain.payments.Payment;
import org.example.domain.resources.Resource;
import org.example.domain.users.User;
import org.example.money.Money;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Booking {
    private String id;
    private User user;
    private Resource resource;
    private LocalDateTime startTime, endTime;
    private BookingStatus bookingStatus;
    private Money calculatedPrice;
    private Payment payment;
    private static final Pattern CODE_PATTERN = Pattern.compile("^BK-(\\d{8})-(\\d{1,3})$");

    public Booking(String id, User user, Resource resource, LocalDateTime startTime, LocalDateTime endTime, Payment payment) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(user, "user cannot be null");
        Objects.requireNonNull(resource, "resource cannot be null");
        Objects.requireNonNull(startTime, "startTime cannot be null");
        Objects.requireNonNull(endTime, "endTime cannot be null");

        isValidBookingId(id);
        if (startTime.isAfter(endTime)) {
            throw new IllegalStateException("start time: " + startTime + " cannot be after end time: " + endTime);
        }

        this.id = id;
        this.user = user;
        this.resource = resource;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = BookingStatus.PENDING;
        this.calculatedPrice = null;
        this.payment = payment;
    }

    public Booking(String id, User user, Resource resource, LocalDateTime startTime, LocalDateTime endTime) {
        this(id, user, resource, startTime, endTime, null);
    }

    private void isValidBookingId(String id) {
        Matcher matcher = CODE_PATTERN.matcher(id);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid booking id: " + id);
        }
        isValidBookingDate(matcher);
    }

    private void isValidBookingDate(Matcher matcher) {
        String datePart = matcher.group(1);
        try {
            LocalDate.parse(datePart, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in booking id: " + datePart);
        }
    }

    public long durationInMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Money getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(Money calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", resource=" + resource +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", bookingStatus=" + bookingStatus +
                ", calculatedPrice=" + calculatedPrice +
                ", payment=" + payment +
                '}';
    }
}
