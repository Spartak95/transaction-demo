package com.xcoder.transaction.service;

import com.xcoder.transaction.entity.Order;
import com.xcoder.transaction.entity.Product;
import com.xcoder.transaction.handler.InventoryHandler;
import com.xcoder.transaction.handler.OrderHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessingService {
    private final OrderHandler orderHandler;
    private final InventoryHandler inventoryHandler;

    public OrderProcessingService(OrderHandler orderHandler, InventoryHandler inventoryHandler) {
        this.orderHandler = orderHandler;
        this.inventoryHandler = inventoryHandler;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Order placeAnOrder(Order order) {
        Product product = inventoryHandler.getProduct(order.getProductId());
        validateStockAvailability(order, product);
        order.setTotalPrice(order.getQuantity() * product.getPrice());
        Order saveOrder = orderHandler.saveOrder(order);
        updateInventoryStock(order, product);
        return saveOrder;
    }

    private void validateStockAvailability(Order order, Product product) {
        if (order.getQuantity() > product.getStockQuantity()) {
            throw new RuntimeException("Insufficient stock!");
        }
    }

    private void updateInventoryStock(Order order, Product product) {
        int availableStock = product.getStockQuantity() - order.getQuantity();
        product.setStockQuantity(availableStock);
        inventoryHandler.updateProductDetails(product);
    }
}
