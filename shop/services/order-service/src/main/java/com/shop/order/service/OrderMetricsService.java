package com.shop.order.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetricsService {

    private final Counter ordersSuccess;
    private final Counter ordersFailed;

    public OrderMetricsService(MeterRegistry registry) {
        this.ordersSuccess = Counter.builder("orders_created_total")
                .description("Total number of successfully created orders")
                .tag("status", "success")
                .register(registry);

        this.ordersFailed = Counter.builder("orders_created_total")
                .description("Total number of failed order creations")
                .tag("status", "failed")
                .register(registry);
    }

    public void incrementSuccess() {
        ordersSuccess.increment();
    }

    public void incrementFailed() {
        ordersFailed.increment();
    }
}
