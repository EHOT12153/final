package com.shop.ui.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CatalogController {

    private final WebClient webClient;

    public CatalogController(WebClient webClient) {
        this.webClient = webClient;
    }

    // Главная страница каталога
    @GetMapping("/")
    public String index(Model model) {
        List<CatalogProduct> apiProducts;
        try {
            apiProducts = webClient.get()
                    .uri("/api/catalog/products")
                    .retrieve()
                    .bodyToFlux(CatalogProduct.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            // catalog-service вернул 4xx/5xx
            model.addAttribute("error",
                    "Не удалось загрузить каталог (код " + e.getStatusCode().value() + ")");
            apiProducts = List.of();
        } catch (Exception e) {
            // Любая другая ошибка: недоступен gateway, DNS, timeout и т.п.
            model.addAttribute("error",
                    "Каталог временно недоступен, попробуйте позже.");
            apiProducts = List.of();
        }

        List<Map<String, Object>> products = apiProducts.stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.getId(),
                        "sku", p.getSku(),
                        "name", p.getName(),
                        "price", p.getPriceCents(),
                        "image", "no-image.png"
                ))
                .collect(Collectors.toList());

        model.addAttribute("products", products);
        return "catalog";
    }

    // Страница карточки товара
    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model) {

        CatalogProduct apiProduct;

        try {
            apiProduct = webClient.get()
                    .uri("/api/catalog/products/{id}", id)
                    .retrieve()
                    .bodyToMono(CatalogProduct.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            model.addAttribute("message", "Товар с id = " + id + " не найден.");
            model.addAttribute("p", null);
            return "product";
        } catch (WebClientResponseException e) {
            model.addAttribute("message",
                    "Ошибка при обращении к каталогу (код " + e.getStatusCode().value() + ")");
            model.addAttribute("p", null);
            return "product";
        } catch (Exception e) {
            // gateway / catalog вообще не доступны
            model.addAttribute("message",
                    "Каталог временно недоступен, попробуйте обновить страницу позже.");
            model.addAttribute("p", null);
            return "product";
        }


        if (apiProduct == null) {
            model.addAttribute("message", "Товар с id = " + id + " не найден.");
            model.addAttribute("p", null);
            return "product";
        }

        Map<String, Object> product = Map.of(
                "id", apiProduct.getId(),
                "sku", apiProduct.getSku(),
                "name", apiProduct.getName(),
                "price", apiProduct.getPriceCents(),
                "image", "no-image.png"
        );

        // ВАЖНО: шаблон product.html использует атрибут 'p'
        model.addAttribute("p", product);
        model.addAttribute("message", null);
        return "product";
    }
}
