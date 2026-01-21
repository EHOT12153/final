package com.shop.cart;

import java.util.Map;

public interface CartRepository {

    /**
     * Получить корзину пользователя в виде productId -> quantity.
     */
    Map<Long, Integer> getCart(Long userId);

    /**
     * Изменить количество товара в корзине на deltaQuantity.
     * Может быть положительным (добавить) или отрицательным (убрать).
     */
    void addItem(Long userId, Long productId, int deltaQuantity);

    /**
     * Удалить товар из корзины полностью.
     */
    void removeItem(Long userId, Long productId);

    /**
     * Очистить корзину пользователя.
     */
    void clearCart(Long userId);
}
