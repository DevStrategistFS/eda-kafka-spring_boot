package com.example.order_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping("/v1/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderCreatedEvent event) {
        orderProducer.orderCreatedEvent(event);
        return ResponseEntity.ok("Order placed successfully with Id " + event.getOrderId());
    }
}
