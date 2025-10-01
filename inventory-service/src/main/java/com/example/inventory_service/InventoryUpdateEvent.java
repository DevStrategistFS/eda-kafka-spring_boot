package com.example.inventory_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class InventoryUpdateEvent {
    private String orderId;
    private String productId;
    private int quantity;
    private InventoryStatus status;
    private String reason;
    private Instant createdAt;
}
