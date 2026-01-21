package com.shop.cart;

public class PaymentResponseDto {

    private Long orderId;
    private String status; // PAID / FAILED

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
