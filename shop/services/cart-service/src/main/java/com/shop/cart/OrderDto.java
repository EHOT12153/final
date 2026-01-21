package com.shop.cart;

public class OrderDto {
    private Long id;
    private Long userId;
    private Integer totalPriceCents;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getTotalPriceCents() { return totalPriceCents; }
    public void setTotalPriceCents(Integer totalPriceCents) { this.totalPriceCents = totalPriceCents; }
}
