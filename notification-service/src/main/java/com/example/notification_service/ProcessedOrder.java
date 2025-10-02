package com.example.notification_service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_orders")
@Data
@NoArgsConstructor
public class ProcessedOrder {

    @Id
    private String orderId;

    private Long processedTime = System.currentTimeMillis();

    public ProcessedOrder(String orderId) {
        this.orderId = orderId;
    }
}
