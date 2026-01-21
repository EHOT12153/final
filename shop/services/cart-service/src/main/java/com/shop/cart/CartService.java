package com.shop.cart;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final String CART_KEY_PREFIX = "cart:";

    private final HashOperations<String, String, Integer> hashOps;

    public CartService(RedisTemplate<String, Integer> redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();
    }

    private String key(Long userId) {
        return CART_KEY_PREFIX + userId;
    }

    public Map<Long, Integer> getCart(Long userId) {
        Map<String, Integer> raw = hashOps.entries(key(userId));
        return raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Long.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));
    }

    public Map<Long, Integer> addItem(Long userId, Long productId, int quantity) {
        String k = key(userId);
        hashOps.increment(k, productId.toString(), quantity);
        return getCart(userId);
    }

    public Map<Long, Integer> removeItem(Long userId, Long productId) {
        String k = key(userId);
        hashOps.delete(k, productId.toString());
        return getCart(userId);
    }

    public void clearCart(Long userId) {
        hashOps.getOperations().delete(key(userId));
    }
}
