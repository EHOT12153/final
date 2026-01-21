package com.shop.order.events;

import com.shop.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventsProducer.class);

    private static final String ORDERS_TOPIC = "orders";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventsProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(Order order) {
        if (order == null || order.getId() == null) {
            log.warn("KAFKA: publishOrderCreated called with null order or id");
            return;
        }

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotalPriceCents()
        );

        log.info(
                "KAFKA: sending OrderCreated event. orderId={}, userId={}, totalPriceCents={}",
                event.getOrderId(), event.getUserId(), event.getTotalPriceCents()
        );

        kafkaTemplate.send(ORDERS_TOPIC, event.getOrderId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("KAFKA: FAILED to send OrderCreated event", ex);
                    } else {
                        log.info(
                                "KAFKA: event sent. topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
    }
}
