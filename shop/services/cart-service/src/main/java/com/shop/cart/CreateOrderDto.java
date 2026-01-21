package com.shop.cart;

import java.util.List;

public class CreateOrderDto {

    private Long userId;
    private Integer totalPriceCents;
    private List<Item> items;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTotalPriceCents() {
        return totalPriceCents;
    }

    public void setTotalPriceCents(Integer totalPriceCents) {
        this.totalPriceCents = totalPriceCents;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    // ---------- вложенный класс ----------
    public static class Item {
        private Long productId;
        private Integer quantity;
        private Integer priceCents;

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

        public Integer getPriceCents() {
            return priceCents;
        }

        public void setPriceCents(Integer priceCents) {
            this.priceCents = priceCents;
        }
    }
}
