package com.shop.payment;

public class PaymentResponse {

    private Long orderId;
    private PaymentStatus status;

    public PaymentResponse(Long orderId, PaymentStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public Long getOrderId() { return orderId; }
    public PaymentStatus getStatus() { return status; }
}
