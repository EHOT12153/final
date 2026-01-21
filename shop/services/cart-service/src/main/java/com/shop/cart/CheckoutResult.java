package com.shop.cart;

public class CheckoutResult {

    private Long userId;
    private Long orderId;
    private Integer totalPriceCents;
    private String paymentStatus;

    // Дополнительные поля для UI / обработки ошибок
    private boolean success;
    private String message;

    public CheckoutResult() {
    }

    public CheckoutResult(Long userId,
                          Long orderId,
                          Integer totalPriceCents,
                          String paymentStatus,
                          boolean success,
                          String message) {
        this.userId = userId;
        this.orderId = orderId;
        this.totalPriceCents = totalPriceCents;
        this.paymentStatus = paymentStatus;
        this.success = success;
        this.message = message;
    }

    // --------- геттеры/сеттеры, которые уже ждёт CheckoutService ---------

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getTotalPriceCents() {
        return totalPriceCents;
    }
    public void setTotalPriceCents(Integer totalPriceCents) {
        this.totalPriceCents = totalPriceCents;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // --------- дополнительные поля для UI/ошибок ---------

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
