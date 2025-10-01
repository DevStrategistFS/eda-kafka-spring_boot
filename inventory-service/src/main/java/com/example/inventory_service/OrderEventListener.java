package com.example.inventory_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
public class OrderEventListener {

    private final InventoryRepository inventoryRepository;
    private final InventoryProducerService inventoryProducerService;

    public OrderEventListener(InventoryRepository inventoryRepository, InventoryProducerService inventoryProducerService) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryProducerService = inventoryProducerService;
    }

    @Transactional
    @KafkaListener(
            topics = "${kafka.topic.orders-created}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderEvent(OrderCreatedEvent event, Acknowledgment ack) {
        InventoryStatus eventStatus;
        String eventReason = "AVAILABLE";
        int requestedQuantity = 1;
        String simulatedProductId = "PROD-" + event.getOrderId().substring(0, 4).toUpperCase();
        try {
            Inventory inventory = inventoryRepository.findById(simulatedProductId)
                    .orElseThrow(() -> new RuntimeException("Product not found " + simulatedProductId));
            int currentStock = inventory.getStockQuantity();
            if (currentStock >= requestedQuantity) {
                inventory.setStockQuantity(currentStock - requestedQuantity);
                inventoryRepository.save(inventory);
                eventStatus = InventoryStatus.INVENTORY_RESERVED;
                log.info("SUCCESS: Stock Available for order {}. New stock for {} : {}", event.getOrderId(), simulatedProductId, inventory.getStockQuantity());
            } else {
                eventStatus = InventoryStatus.INVENTORY_FAILURE;
                eventReason = "INSUFFICIENT_STOCK";
                log.error("FAILURE: INSUFFICIENT STOCK TO PLACE ORDER FOR {}", event.getOrderId());
            }
        } catch (RuntimeException e) {
            log.error("Error during handling order event: {}", e.getMessage());
            throw new RuntimeException("Processing failed, rolling back: " + e.getMessage());
        }
        InventoryUpdateEvent inventoryUpdateEvent = InventoryUpdateEvent.builder()
                .orderId(event.getOrderId())
                .productId(simulatedProductId)
                .quantity(requestedQuantity)
                .status(eventStatus)
                .reason(eventReason)
                .createdAt(Instant.now())
                .build();
        inventoryProducerService.sendEvent(inventoryUpdateEvent);
        ack.acknowledge();
    }
}
