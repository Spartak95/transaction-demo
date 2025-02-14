package com.xcoder.transaction.handler;

import com.xcoder.transaction.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationHandler {

    @Transactional(propagation = Propagation.NEVER)
    public void sendOrderConfirmation(Order order) {
        // Send an email notification to the customer
        System.out.println(order.getId() + " Order placed successfully");
    }
}
