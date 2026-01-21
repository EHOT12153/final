package com.shop.order.domain;

import java.util.List;

public class CreateOrderRequest {

    private Long userId;
    private Long totalPriceCents;
    private List<Item> items;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(Long userId, Long totalPriceCents, List<Item> items) {
        this.userId = userId;
        this.totalPriceCents = totalPriceCents;
        this.items = items;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTotalPriceCents() {
        return totalPriceCents;
    }

    public void setTotalPriceCents(Long totalPriceCents) {
        this.totalPriceCents = totalPriceCents;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    // Вложенный DTO для позиции заказа
    public static class Item {
        private Long productId;
        private Integer quantity;
        private Long priceCents;

        public Item() {
        }

        public Item(Long productId, Integer quantity, Long priceCents) {
            this.productId = productId;
            this.quantity = quantity;
            this.priceCents = priceCents;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Long getPriceCents() {
            return priceCents;
        }

        public void setPriceCents(Long priceCents) {
            this.priceCents = priceCents;
        }
    }
}
