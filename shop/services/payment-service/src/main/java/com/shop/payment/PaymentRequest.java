package com.shop.payment;

import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private Integer amountCents;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Integer getAmountCents() { return amountCents; }
    public void setAmountCents(Integer amountCents) { this.amountCents = amountCents; }
}
