package com.xcoder.transaction.controller;

import com.xcoder.transaction.entity.Order;
import com.xcoder.transaction.service.OrderProcessingService;
import com.xcoder.transaction.service.isolation.ReadCommittedDemo;
import com.xcoder.transaction.service.isolation.ReadUncommittedDemo;
import com.xcoder.transaction.service.isolation.RepeatableCommittedDemo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderProcessingController {
    private final OrderProcessingService orderProcessingService;
    private final ReadUncommittedDemo readUncommittedDemo;
    private final ReadCommittedDemo readCommittedDemo;
    private final RepeatableCommittedDemo repeatableCommittedDemo;

    public OrderProcessingController(OrderProcessingService orderProcessingService,
                                     ReadUncommittedDemo readUncommittedDemo,
                                     ReadCommittedDemo readCommittedDemo,
                                     RepeatableCommittedDemo repeatableCommittedDemo) {
        this.orderProcessingService = orderProcessingService;
        this.readUncommittedDemo = readUncommittedDemo;
        this.readCommittedDemo = readCommittedDemo;
        this.repeatableCommittedDemo = repeatableCommittedDemo;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderProcessingService.placeAnOrder(order));
    }

    @GetMapping("/isolation")
    public String testIsolation() throws InterruptedException {
        //readUncommittedDemo.testReadUncommitted(1);
        //readCommittedDemo.testReadCommitted(1);
        repeatableCommittedDemo.demonstrateRepeatableRead(1);
        return "success";
    }
}
