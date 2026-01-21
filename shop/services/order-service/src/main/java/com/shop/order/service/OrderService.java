package com.shop.order.service;

import com.shop.order.CreateOrderRequest;
import com.shop.order.Order;
import com.shop.order.OrderItem;
import com.shop.order.OrderItemRequest;
import com.shop.order.OrderRepository;
import com.shop.order.events.OrderEventsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventsProducer orderEventsProducer;
    private final OrderMetricsService orderMetricsService;

    public OrderService(OrderRepository orderRepository,
                        OrderEventsProducer orderEventsProducer,
                        OrderMetricsService orderMetricsService) {
        this.orderRepository = orderRepository;
        this.orderEventsProducer = orderEventsProducer;
        this.orderMetricsService = orderMetricsService;
    }

    /**
     * Создание заказа + бизнес-метрики + публикация события в Kafka.
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateOrderRequest is null");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId is null");
        }

        List<OrderItemRequest> itemRequests = request.getItems();
        int itemsCount = (itemRequests != null) ? itemRequests.size() : 0;

        log.info("ORDER-SERVICE: creating order for userId={} with {} items",
                request.getUserId(), itemsCount);

        try {
            if (itemRequests == null || itemRequests.isEmpty()) {
                throw new IllegalArgumentException("Order items list is empty");
            }

            Order order = new Order();
            order.setUserId(request.getUserId());
            order.setCreatedAt(Instant.now());

            long total = 0L;

            for (OrderItemRequest itemReq : itemRequests) {
                if (itemReq == null) {
                    continue;
                }

                // productId, скорее всего, Long — его можно проверить на null
                if (itemReq.getProductId() == null) {
                    throw new IllegalArgumentException("Order item productId is null");
                }

                // quantity и priceCents — примитивы int → только проверка значения
                if (itemReq.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Order item quantity must be > 0");
                }
                if (itemReq.getPriceCents() < 0) {
                    throw new IllegalArgumentException("Order item priceCents must be >= 0");
                }

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProductId(itemReq.getProductId());
                item.setQuantity(itemReq.getQuantity());
                item.setPriceCents(itemReq.getPriceCents());

                order.getItems().add(item);

                total += (long) itemReq.getPriceCents() * itemReq.getQuantity();
            }

            if (order.getItems().isEmpty()) {
                throw new IllegalArgumentException("No valid order items to create order");
            }

            order.setTotalPriceCents(total);

            Order saved = orderRepository.save(order);

            log.info("ORDER-SERVICE: order saved. id={}, userId={}, totalPriceCents={}",
                    saved.getId(), saved.getUserId(), saved.getTotalPriceCents()
            );

            // бизнес-метрика — успешный заказ (сохранение в БД)
            orderMetricsService.incrementSuccess();

            // Публикация события в Kafka в отдельном try/catch,
            // чтобы ошибка Kafka не откатывала транзакцию заказа.
            try {
                orderEventsProducer.publishOrderCreated(saved);
                log.info("ORDER-SERVICE: Kafka event ORDER_CREATED published for orderId={}", saved.getId());
            } catch (Exception kafkaEx) {
                log.error("ORDER-SERVICE: order {} created, but failed to publish Kafka event",
                        saved.getId(), kafkaEx);
            }

            return saved;

        } catch (Exception ex) {
            orderMetricsService.incrementFailed();
            log.error("ORDER-SERVICE: failed to create order for userId={}", request.getUserId(), ex);
            throw ex;
        }
    }
}
