package org.example.domain.bookings;

import org.example.domain.payments.Payment;
import org.example.domain.resources.Device;
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
    private final String id;
    private final User user;
    private final Resource resource;
    private final LocalDateTime startTime, endTime;
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
        if (!(startTime.isBefore(endTime))) {
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

    public void confirmBooking() {
        if (!(getBookingStatus().equals(BookingStatus.PENDING))) {
            throw new IllegalStateException("booking: " + getId() + " cannot be confirmed, must be pending");
        }
        setBookingStatus(BookingStatus.CONFIRMED);
    }

    public void cancelBooking() {
        if (!(getBookingStatus().equals(BookingStatus.PENDING) || getBookingStatus().equals(BookingStatus.CONFIRMED))) {
            throw new IllegalStateException("booking: " + getId() + " cannot be cancelled, must be pending or confirmed");
        }
        if (getResource() instanceof Device device) {
            device.setRemainingQuantity(device.getRemainingQuantity() + 1);
        }
        setBookingStatus(BookingStatus.CANCELLED);
    }

    public void completeBooking() {
        if (!(getBookingStatus().equals(BookingStatus.CONFIRMED))) {
            throw new IllegalStateException("booking: " + getId() + " cannot be completed, must be confirmed");
        }
        if (getResource() instanceof Device device) {
            device.setRemainingQuantity(device.getRemainingQuantity() + 1);
        }
        setBookingStatus(BookingStatus.COMPLETED);
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Resource getResource() {
        return resource;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
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
