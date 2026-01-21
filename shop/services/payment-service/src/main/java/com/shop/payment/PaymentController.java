package com.shop.payment;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentMetricsService paymentMetricsService;

    public PaymentController(PaymentMetricsService paymentMetricsService) {
        this.paymentMetricsService = paymentMetricsService;
    }

    @PostMapping
    public PaymentResponse pay(@RequestBody @Valid PaymentRequest request) {
        try {
            // заглушка: всегда успешная оплата
            PaymentResponse response =
                    new PaymentResponse(request.getOrderId(), PaymentStatus.PAID);

            // метрика успешного платежа
            paymentMetricsService.incrementSuccess();

            return response;
        } catch (Exception ex) {
            // если когда-нибудь тут появится реальная логика и будет ошибка — считаем как failed
            paymentMetricsService.incrementFailed();
            throw ex;
        }
    }
}
