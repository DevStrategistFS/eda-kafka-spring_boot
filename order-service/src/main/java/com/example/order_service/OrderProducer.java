package com.example.order_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderProducer {

    @Value("${spring.kafka.topic.orders-created}")
    private String ordersTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void orderCreatedEvent(OrderCreatedEvent event) {
        log.info("Preparing to send OrderCreatedEvent for Id: {}", event.getOrderId());
        kafkaTemplate.send(ordersTopic, event.getOrderId(), event)
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
