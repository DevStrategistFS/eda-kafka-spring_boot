package com.example.notification_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class NotificationService {

    private final ProcessedOrderRepository processedOrderRepository;

    public NotificationService(ProcessedOrderRepository processedOrderRepository) {
        this.processedOrderRepository = processedOrderRepository;
    }

    @Transactional
    public void processAndNotify(InventoryUpdateEvent event, Acknowledgment ack) {
        String orderId = event.getOrderId();
        if(processedOrderRepository.existsById(orderId)) {
            log.warn("Order {} already processed. Skipping Notification", orderId);
            ack.acknowledge();
            return;
        }
        InventoryStatus status = event.getStatus();
        if(InventoryStatus.INVENTORY_RESERVED.equals(status)) {
            log.info("SUCCESS: Sending Inventory Reserved email for Order {} and Product {}", orderId, event.getProductId());
        } else if (InventoryStatus.INVENTORY_FAILURE.equals(status)) {
            log.info("FAILURE: Sending Inventory Failure email for Order {}. Reason {}", orderId, event.getReason());
        } else {
            log.warn("Unknown Inventory Status {} received. Skipping Notification.", status);
        }
        processedOrderRepository.save(new ProcessedOrder(orderId));
        log.info("Order {} successfully processed.", orderId);
        ack.acknowledge();
    }
}
