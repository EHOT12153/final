package com.shop.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // уже было:
    // interface OrderRepository extends JpaRepository<Order, Long> {}

    // ДОБАВЬ вот это:
    List<Order> findByUserId(Long userId);
}
