package com.shop.cart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final CheckoutService checkoutService;

    public CartController(CartService cartService,
                          CheckoutService checkoutService) {
        this.cartService = cartService;
        this.checkoutService = checkoutService;
    }

    // -------------------------------------------------------------
    // GET /cart/{userId}
    // -------------------------------------------------------------

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        try {
            Map<Long, Integer> items = cartService.getCart(userId);

            CartResponse response = new CartResponse();
            response.setUserId(userId);
            response.setItems(items != null ? items : Collections.emptyMap());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка получения корзины userId={}", userId, e);

            CartResponse r = new CartResponse();
            r.setUserId(userId);
            r.setItems(Collections.emptyMap());

            return ResponseEntity
                    .status(503)
                    .body(r);
        }
    }

    // -------------------------------------------------------------
    // POST /cart/{userId}/items
    // -------------------------------------------------------------

    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addItem(@PathVariable Long userId,
                                        @RequestBody AddItemRequest request) {
        try {
            if (request.getProductId() == null || request.getQuantity() == null) {
                return ResponseEntity.badRequest().build();
            }

            int qty = request.getQuantity();
            if (qty == 0) {
                return ResponseEntity.ok().build();
            }

            cartService.addItem(userId, request.getProductId(), qty);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Ошибка добавления товара в корзину: userId={}, productId={}",
                    userId, request.getProductId(), e);
            return ResponseEntity.status(503).build();
        }
    }

    // -------------------------------------------------------------
    // DELETE /cart/{userId}/items/{productId}
    // -------------------------------------------------------------

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long userId,
                                           @PathVariable Long productId) {
        try {
            cartService.removeItem(userId, productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка удаления товара из корзины: userId={}, productId={}",
                    userId, productId, e);
            return ResponseEntity.status(503).build();
        }
    }

    // -------------------------------------------------------------
    // DELETE /cart/{userId}
    // -------------------------------------------------------------

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка очистки корзины userId={}", userId, e);
            return ResponseEntity.status(503).build();
        }
    }

    // -------------------------------------------------------------
    // POST /cart/{userId}/checkout
    // -------------------------------------------------------------

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<CheckoutResult> checkout(@PathVariable Long userId) {
        try {
            CheckoutResult result = checkoutService.checkout(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Ошибка checkout userId={}", userId, e);

            CheckoutResult r = new CheckoutResult();
            r.setOrderId(null);
            r.setSuccess(false);
            r.setMessage("Сервис оформления заказа временно недоступен");

            return ResponseEntity.status(503).body(r);
        }
    }

    // ====================== DTO ======================

    public static class CartResponse {
        private Long userId;
        private Map<Long, Integer> items;

        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        public Map<Long, Integer> getItems() {
            return items;
        }
        public void setItems(Map<Long, Integer> items) {
            this.items = items;
        }
    }

    public static class AddItemRequest {
        private Long productId;
        private Integer quantity;

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
    }
}
