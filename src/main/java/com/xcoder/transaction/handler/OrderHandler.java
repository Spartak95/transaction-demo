package com.xcoder.transaction.handler;

import com.xcoder.transaction.entity.Order;
import com.xcoder.transaction.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderHandler {
    private final OrderRepository orderRepository;

    public OrderHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
