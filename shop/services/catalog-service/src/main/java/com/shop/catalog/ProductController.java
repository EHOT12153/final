package com.shop.catalog;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final JdbcTemplate jdbc;

    public ProductController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Список всех продуктов
    @GetMapping
    public List<Map<String, Object>> getAll() {
        return jdbc.queryForList(
                "SELECT id, sku, name, price_cents FROM products ORDER BY id"
        );
    }

    // Один продукт по id
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOne(@PathVariable("id") Long id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, sku, name, price_cents FROM products WHERE id = ?",
                id
        );

        if (rows.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rows.get(0));
    }

}
