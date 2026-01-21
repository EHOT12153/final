package com.shop.ui.web;

import com.shop.ui.cart.CheckoutResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class CartController {

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

    /**
     * "Авторизация без авторизации": изолируем корзины по HttpSession.
     *
     * Раньше тут был DEMO_USER_ID=1, из-за чего ВСЕ пользователи делили одну корзину.
     */
    private static final String SESSION_USER_ID_ATTR = "SHOP_DEMO_USER_ID";
    private static final AtomicLong USER_SEQ = new AtomicLong(1_000L);

    private long getOrCreateUserId(HttpSession session) {
        Object existing = session.getAttribute(SESSION_USER_ID_ATTR);
        if (existing instanceof Long id) {
            return id;
        }
        long newId = USER_SEQ.getAndIncrement();
        session.setAttribute(SESSION_USER_ID_ATTR, newId);
        return newId;
    }

    public CartController(RestTemplate restTemplate,
                          @Value("${shop.api.base-url}") String apiBaseUrl) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
    }

    // ----- Показ корзины -----

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        long userId = getOrCreateUserId(session);
        String cartUrl = apiBaseUrl + "/api/cart/" + userId;

        CartResponseDto cart;
        try {
            cart = restTemplate.getForObject(cartUrl, CartResponseDto.class);
        } catch (RestClientException ex) {
            model.addAttribute("items", List.of());
            model.addAttribute("total", 0);
            model.addAttribute("error", "Не удалось загрузить корзину");
            return "cart";
        }

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            model.addAttribute("items", List.of());
            model.addAttribute("total", 0);
            model.addAttribute("error", null);
            return "cart";
        }

        List<Map<String, Object>> items = new ArrayList<>();
        int total = 0;

        for (Map.Entry<Long, Integer> entry : cart.getItems().entrySet()) {
            Long productId = entry.getKey();
            Integer qty = entry.getValue();
            if (qty == null || qty <= 0) continue;

            // Тянем данные товара из каталога
            String productUrl = apiBaseUrl + "/api/catalog/products/" + productId;
            CatalogProduct product;
            try {
                product = restTemplate.getForObject(productUrl, CatalogProduct.class);
            } catch (RestClientException ex) {
                // Если не смогли получить товар – просто пропускаем
                continue;
            }
            if (product == null) continue;

            int price = product.getPriceCents() != null ? product.getPriceCents() : 0;
            int lineTotal = price * qty;

            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getId());
            item.put("name", product.getName());
            item.put("price", price);
            item.put("qty", qty);
            item.put("lineTotal", lineTotal);

            items.add(item);
            total += lineTotal;
        }

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("error", null);
        return "cart";
    }

    // ----- Добавление товара в корзину из product.html -----

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(name = "qty", defaultValue = "1") Integer qty,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        long userId = getOrCreateUserId(session);
        if (qty == null || qty < 1) {
            qty = 1;
        }

        String url = apiBaseUrl + "/api/cart/" + userId + "/items";

        AddItemRequestDto body = new AddItemRequestDto();
        body.setProductId(productId);
        body.setQuantity(qty);

        try {
            restTemplate.postForObject(url, body, Void.class);
        } catch (RestClientException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Не удалось добавить товар в корзину");
        }

        return "redirect:/cart";
    }

    // ----- Удаление одной позиции -----

    @PostMapping("/cart/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") Long productId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        long userId = getOrCreateUserId(session);
        String url = apiBaseUrl + "/api/cart/" + userId + "/items/" + productId;

        try {
            restTemplate.delete(url);
        } catch (RestClientException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить товар из корзины");
        }

        return "redirect:/cart";
    }

    // ----- Очистка корзины -----

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        long userId = getOrCreateUserId(session);
        String url = apiBaseUrl + "/api/cart/" + userId;

        try {
            restTemplate.delete(url);
        } catch (RestClientException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Не удалось очистить корзину");
        }

        return "redirect:/cart";
    }

    // ----- Оформление заказа -----

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        long userId = getOrCreateUserId(session);
        String url = apiBaseUrl + "/api/cart/" + userId + "/checkout";

        try {
            CheckoutResultDto result =
                    restTemplate.postForObject(url, null, CheckoutResultDto.class);

            if (result != null && result.getOrderId() != null) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        null
                );
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Заказ оформлен. Номер заказа: " + result.getOrderId()
                );
            }
        } catch (RestClientException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Не удалось оформить заказ");
        }

        return "redirect:/cart";
    }

    // ----- Внутренние DTO для работы с API -----

    // Ответ cart-service: { "userId": ..., "items": { "<productId>": qty, ... } }
    public static class CartResponseDto {
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

    // Тело запроса на добавление: { "productId": ..., "quantity": ... }
    public static class AddItemRequestDto {
        private Long productId;
        private int quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
