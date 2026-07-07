package org.example.domain.payments;

import org.example.domain.bookings.Booking;
import org.example.domain.users.User;
import org.example.money.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Invoice {
    private String invoiceId;
    private LocalDateTime invoiceDate;
    private User buyer;
    private Money total;
    private String itemDescription;
    private static int counter = 0;
    private static final Pattern CODE_PATTERN = Pattern.compile("^INV-(\\d{8})-(\\d{1,3})$");


    public Invoice(LocalDateTime invoiceDate, User buyer, Money total, String itemDescription) {
        Objects.requireNonNull(invoiceDate, "invoiceDate cannot be null");
        Objects.requireNonNull(buyer, "buyer cannot be null");
        Objects.requireNonNull(itemDescription, "itemDescription cannot be null");

        if (invoiceDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("invoiceDate cannot be in the future: " + invoiceDate);
        }
        if (itemDescription.length() < 5){
            throw new IllegalArgumentException("item description is too short: " + itemDescription);
        }

        String invoiceId = "BK-" + invoiceDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + counter++;
        isValidInvoiceId(invoiceId);

        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.buyer = buyer;
        this.total = total;
        this.itemDescription = itemDescription;
    }

    private void isValidInvoiceId(String id) {
        Matcher matcher = CODE_PATTERN.matcher(id);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid booking id: " + id);
        }
        isValidInvoiceDate(id);
    }

    private void isValidInvoiceDate(String id) {
        Matcher matcher = CODE_PATTERN.matcher(id);

        String datePart = matcher.group(1);

        try {
            LocalDate.parse(datePart, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in booking id: " + datePart);
        }
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public Money getTotal() {
        return total;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", buyer=" + buyer +
                ", total=" + total +
                ", itemDescription='" + itemDescription + '\'' +
                '}';
    }
}
