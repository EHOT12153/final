package com.shop.cart;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    private final CartService cartService;
    private final RestTemplate restTemplate;

    // URL других микросервисов по именам сервисов в docker-compose
    private static final String CATALOG_URL = "http://catalog-service:8080/products";
    private static final String ORDER_URL   = "http://order-service:8080/orders";
    private static final String PAYMENT_URL = "http://payment-service:8080/payments";

    public CheckoutService(CartService cartService, RestTemplate restTemplate) {
        this.cartService = cartService;
        this.restTemplate = restTemplate;
    }

    public CheckoutResult checkout(Long userId) {
        // 1. Забираем корзину пользователя
        Map<Long, Integer> cart = cartService.getCart(userId);
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // 2. Подтягиваем все продукты из каталога
        ProductSummary[] productsArray =
                restTemplate.getForObject(CATALOG_URL, ProductSummary[].class);

        if (productsArray == null || productsArray.length == 0) {
            throw new IllegalStateException("Catalog returned no products");
        }

        Map<Long, ProductSummary> productsById = Arrays.stream(productsArray)
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(ProductSummary::getId, Function.identity()));

        // 3. Готовим позиции заказа и считаем сумму
        List<CreateOrderDto.Item> items = new ArrayList<>();
        int total = 0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue() != null ? entry.getValue() : 0;

            if (qty <= 0) {
                continue;
            }

            ProductSummary product = productsById.get(productId);
            if (product == null) {
                throw new IllegalStateException("Product not found in catalog: " + productId);
            }

            Integer priceCentsObj = product.getPriceCents();
            if (priceCentsObj == null) {
                throw new IllegalStateException(
                        "Product " + productId + " (" + product.getName()
                                + ") has null priceCents from catalog-service"
                );
            }

            int priceCents = priceCentsObj;

            CreateOrderDto.Item item = new CreateOrderDto.Item();
            item.setProductId(productId);
            item.setQuantity(qty);
            item.setPriceCents(priceCents);

            items.add(item);
            total += priceCents * qty;
        }

        if (items.isEmpty()) {
            throw new IllegalStateException("No valid items in cart");
        }

        // 4. Создаём заказ в order-service
        CreateOrderDto createOrderDto = new CreateOrderDto();
        createOrderDto.setUserId(userId);
        createOrderDto.setItems(items);
        createOrderDto.setTotalPriceCents(total);

        OrderDto order = restTemplate.postForObject(ORDER_URL, createOrderDto, OrderDto.class);
        if (order == null || order.getId() == null) {
            throw new IllegalStateException("Order service returned no order");
        }

        Integer orderTotal = order.getTotalPriceCents();
        int amountToPay = (orderTotal != null) ? orderTotal : total;

        // 5. Оплата в payment-service
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", order.getId());
        paymentRequest.put("amountCents", amountToPay);

        PaymentResponseDto payment = restTemplate.postForObject(
                PAYMENT_URL, paymentRequest, PaymentResponseDto.class
        );

        String paymentStatus = (payment != null && payment.getStatus() != null)
                ? payment.getStatus()
                : "UNKNOWN";

        // 6. Чистим корзину
        cartService.clearCart(userId);

        // 7. Собираем итоговый ответ
        CheckoutResult result = new CheckoutResult();
        result.setUserId(userId);
        result.setOrderId(order.getId());
        result.setTotalPriceCents(amountToPay);
        result.setPaymentStatus(paymentStatus);

        // тут и была проблема: не проставляли success/message
        boolean success = "PAID".equalsIgnoreCase(paymentStatus);
        result.setSuccess(success);
        if (!success) {
            result.setMessage("Оплата не подтверждена, статус: " + paymentStatus);
        }

        return result;
    }
}
