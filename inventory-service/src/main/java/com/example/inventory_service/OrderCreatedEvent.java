package com.example.inventory_service;

import lombok.Data;

import java.time.Instant;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private Long customerId;
    private Double totalAmount;
    private Instant createdAt;
}
