package com.shop.ui.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class OrderController {

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

    /**
     * "Авторизация без авторизации": изолируем пользователя по HttpSession.
     *
     * Это должно быть согласовано с CartController (тот же атрибут сессии).
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

    public OrderController(RestTemplate restTemplate,
                           @Value("${shop.api.base-url}") String apiBaseUrl) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
    }

    // Список заказов пользователя
    @GetMapping("/orders")
    public String listOrders(Model model, HttpSession session) {
        long userId = getOrCreateUserId(session);
        String url = apiBaseUrl + "/api/orders/user/" + userId;

        List<Map<String, Object>> orders;
        try {
            // Забираем List<Order> как List<Map> — без DTO, проще
            orders = restTemplate.getForObject(url, List.class);
        } catch (RestClientException ex) {
            ex.printStackTrace();
            model.addAttribute("orders", Collections.emptyList());
            model.addAttribute("error", "Не удалось загрузить список заказов");
            return "orders";
        }

        if (orders == null) {
            orders = Collections.emptyList();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("error", null);
        return "orders";
    }

    // Детали одного заказа
    @GetMapping("/orders/{orderId}")
    public String viewOrder(@PathVariable Long orderId, Model model) {
        String url = apiBaseUrl + "/api/orders/" + orderId;

        Map<String, Object> order;
        try {
            order = restTemplate.getForObject(url, Map.class);
        } catch (RestClientException ex) {
            ex.printStackTrace();
            model.addAttribute("order", null);
            model.addAttribute("error", "Не удалось загрузить заказ");
            return "order";
        }

        if (order == null) {
            model.addAttribute("order", null);
            model.addAttribute("error", "Заказ не найден");
        } else {
            model.addAttribute("order", order);
            model.addAttribute("error", null);
        }

        return "order";
    }
}
