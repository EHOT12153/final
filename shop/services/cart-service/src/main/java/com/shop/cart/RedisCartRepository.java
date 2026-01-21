package com.shop.cart;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RedisCartRepository implements CartRepository {

    private final StringRedisTemplate redis;

    public RedisCartRepository(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private String key(Long userId) {
        return "cart:" + userId;
    }

    @Override
    public Map<Long, Integer> getCart(Long userId) {
        Map<Object, Object> entries = redis.opsForHash().entries(key(userId));

        Map<Long, Integer> result = new HashMap<>();
        for (Map.Entry<Object, Object> e : entries.entrySet()) {
            Long productId = Long.valueOf(e.getKey().toString());
            Integer qty = Integer.valueOf(e.getValue().toString());
            result.put(productId, qty);
        }
        return result;
    }

    @Override
    public void addItem(Long userId, Long productId, int deltaQuantity) {
        if (deltaQuantity == 0) {
            return;
        }

        String k = key(userId);
        String field = productId.toString();

        Long newQty = redis.opsForHash().increment(k, field, deltaQuantity);

        // если ушли в 0 или ниже – удаляем позицию
        if (newQty != null && newQty <= 0) {
            redis.opsForHash().delete(k, field);
        }
    }

    @Override
    public void removeItem(Long userId, Long productId) {
        redis.opsForHash().delete(key(userId), productId.toString());
    }

    @Override
    public void clearCart(Long userId) {
        redis.delete(key(userId));
    }
}
