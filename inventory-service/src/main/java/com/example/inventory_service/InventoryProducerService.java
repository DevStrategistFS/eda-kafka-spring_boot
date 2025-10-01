package com.example.inventory_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryProducerService {

    @Value("${inventory.topic.updates}")
    private String inventoryTopic;

    private final KafkaTemplate<String, InventoryUpdateEvent> kafkaTemplate;

    public InventoryProducerService(KafkaTemplate<String, InventoryUpdateEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(InventoryUpdateEvent event) {
        log.info("Preparing to send InventoryUpdateEvent for Id: {}", event.getOrderId());
        kafkaTemplate.send(inventoryTopic, event.getOrderId(), event)
                .whenComplete((result, ex) -> {
                    if (null == ex) {
                        log.info("SUCCESS: Message sent to topic {}, partition {}, offset {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("FAILURE: Unable to send message for order Id {}, {}",
                                event.getOrderId(), ex.getMessage());
                    }
                });
    }
}
