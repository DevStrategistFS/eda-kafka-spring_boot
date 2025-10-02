package com.example.notification_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryUpdateConsumer {

    private final NotificationService notificationService;

    public InventoryUpdateConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.inventory-updates}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(InventoryUpdateEvent event, Acknowledgment ack) {
        log.info("Received event for Order: {} with Status: {}", event.getOrderId(), event.getStatus());
        try {
            notificationService.processAndNotify(event, ack);
        } catch (Exception e) {
            log.error("Failed to process event for Order {}. Offset will not be committed.", event.getOrderId());
        }
    }
}
