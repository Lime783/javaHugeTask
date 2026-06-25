package org.example.domain.payments;

import org.example.domain.users.User;
import org.example.money.Money;

import java.time.LocalDateTime;
import java.util.Objects;

public class Invoice {
    private String invoiceId;
    private LocalDateTime invoiceDate;
    private User buyer;
    private Money total;
    private String itemDescription;

    public Invoice(String invoiceId, LocalDateTime invoiceDate, User buyer, Money total, String itemDescription) {
        // TODO: regex na invoiceId
        Objects.requireNonNull(invoiceId, "invoiceId cannot be null");
        Objects.requireNonNull(invoiceDate, "invoiceDate cannot be null");
        Objects.requireNonNull(buyer, "buyer cannot be null");
        Objects.requireNonNull(itemDescription, "itemDescription cannot be null");

        if (invoiceDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("invoiceDate cannot be in the future: " + invoiceDate);
        }
        if (itemDescription.length() < 5){
            throw new IllegalArgumentException("item description is too short: " + itemDescription);
        }
        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.buyer = buyer;
        this.total = total;
        this.itemDescription = itemDescription;
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
}
