package com.example.inventory_service;

import lombok.Data;

import java.time.Instant;

@Data
public class InventoryUpdateEvent {
    private String orderId;
    private String productId;
    private int quantity;
    private String status;
    private String reason;
    private Instant createdAt;
}
