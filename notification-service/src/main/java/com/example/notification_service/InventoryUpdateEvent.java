package com.example.notification_service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateEvent {
    private String orderId;
    private String productId;
    private int quantity;
    private InventoryStatus status;
    private String reason;
    private Instant createdAt;
}
