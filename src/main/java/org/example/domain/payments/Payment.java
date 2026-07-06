package org.example.domain.payments;

import org.example.money.Money;

public abstract class Payment {
    protected Money amount;
    protected String paymentId;
    protected PaymentStatus paymentStatus;

    protected abstract void capture();
}
