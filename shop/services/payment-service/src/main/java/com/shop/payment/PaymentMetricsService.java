package com.shop.payment;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class PaymentMetricsService {

    private final Counter paymentsSuccess;
    private final Counter paymentsFailed;

    public PaymentMetricsService(MeterRegistry registry) {
        this.paymentsSuccess = Counter.builder("payments_total")
                .description("Total number of payments")
                .tag("status", "success")
                .register(registry);

        this.paymentsFailed = Counter.builder("payments_total")
                .description("Total number of failed payments")
                .tag("status", "failed")
                .register(registry);
    }

    public void incrementSuccess() {
        paymentsSuccess.increment();
    }

    public void incrementFailed() {
        paymentsFailed.increment();
    }
}
