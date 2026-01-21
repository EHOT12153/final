package com.shop.order;

import com.shop.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orders;
    private final OrderService orderService;

    public OrderController(OrderRepository orders, OrderService orderService) {
        this.orders = orders;
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody @Valid CreateOrderRequest request) {
        // ВАЖНО: используем OrderService, там уже логика + отправка события в Kafka
        return orderService.createOrder(request);
    }

    @GetMapping("/user/{userId}")
    public java.util.List<Order> listByUser(@PathVariable Long userId) {
        return orders.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) {
        return orders.findById(id).orElseThrow();
    }
}
