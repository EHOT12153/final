package com.shop.order.events;

public class OrderCreatedEvent {

    private Long orderId;
    private Long userId;
    private Long totalPriceCents;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long orderId, Long userId, Long totalPriceCents) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPriceCents = totalPriceCents;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTotalPriceCents() {
        return totalPriceCents;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTotalPriceCents(Long totalPriceCents) {
        this.totalPriceCents = totalPriceCents;
    }
}
