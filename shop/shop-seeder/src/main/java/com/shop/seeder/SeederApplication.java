package com.shop.seeder;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class SeederApplication implements CommandLineRunner {

    private final WebClient webClient;

    public SeederApplication(@Value("${app.gateway-base}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SeederApplication.class, args);
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < 200; i++) {
            Map<String, Object> p = Map.of(
                    "title", "Product-" + i,
                    "price", ThreadLocalRandom.current().nextInt(10, 1000),
                    "imageUrl", "https://picsum.photos/seed/" + i + "/400/300",
                    "description", "Demo product " + i
            );
            try {
                webClient.post()
                        .uri("/admin/products")
                        .bodyValue(p)
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            } catch (Exception e) {
                System.err.println("Error at " + i + ": " + e.getMessage());
            }
        }
        System.out.println("Seeding complete.");
    }
}
