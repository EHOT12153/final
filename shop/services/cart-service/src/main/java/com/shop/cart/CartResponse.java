package com.shop.cart;

import java.util.Map;

public class CartResponse {

    private Long userId;
    private Map<Long, Integer> items;

    public CartResponse(Long userId, Map<Long, Integer> items) {
        this.userId = userId;
        this.items = items;
    }

    public Long getUserId() { return userId; }
    public Map<Long, Integer> getItems() { return items; }
}
