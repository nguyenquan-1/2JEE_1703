package com.example.quanlysach2.repository;

import com.example.quanlysach2.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}