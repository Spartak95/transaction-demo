package com.xcoder.transaction.service;

import com.xcoder.transaction.entity.Order;
import com.xcoder.transaction.entity.Product;
import com.xcoder.transaction.exception.InsufficientStockException;
import com.xcoder.transaction.handler.AuditLogHandler;
import com.xcoder.transaction.handler.InventoryHandler;
import com.xcoder.transaction.handler.NotificationHandler;
import com.xcoder.transaction.handler.OrderHandler;
import com.xcoder.transaction.handler.PaymentValidatorHandler;
import com.xcoder.transaction.handler.ProductRecommendationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessingService {
    private final OrderHandler orderHandler;
    private final AuditLogHandler auditLogHandler;
    private final InventoryHandler inventoryHandler;
    private final PaymentValidatorHandler paymentValidatorHandler;
    private final NotificationHandler notificationHandler;
    private final ProductRecommendationHandler productRecommendationHandler;

    public OrderProcessingService(OrderHandler orderHandler, AuditLogHandler auditLogHandler,
                                  InventoryHandler inventoryHandler, PaymentValidatorHandler paymentValidatorHandler,
                                  NotificationHandler notificationHandler,
                                  ProductRecommendationHandler productRecommendationHandler) {
        this.orderHandler = orderHandler;
        this.auditLogHandler = auditLogHandler;
        this.inventoryHandler = inventoryHandler;
        this.paymentValidatorHandler = paymentValidatorHandler;
        this.notificationHandler = notificationHandler;
        this.productRecommendationHandler = productRecommendationHandler;
    }

    // REQUIRED - join an existing transaction or create a new one if not exist
    // REQUIRES_NEW - always creates a new transaction, suspending any existing transaction
    // MANDATORY - require an existing transaction, if nothing found it will throw exception
    // NEVER - ensure the method will run without transaction, throw an exception if found any
    // NOT_SUPPORTED - execute method without transaction, suspending any active transaction
    // SUPPORTS - supports the current transaction if exists; otherwise, executes non-transactional
    // NESTED - execute within a nested transaction, allowing nested transaction to rollback independently
    // if there is any exception without impacting outer transaction

    // outer transaction
    @Transactional(propagation = Propagation.REQUIRED)
    public Order placeAnOrder(Order order) {
        Product product = inventoryHandler.getProduct(order.getProductId());
        validateStockAvailability(order, product);
        order.setTotalPrice(order.getQuantity() * product.getPrice());
        Order saveOrder = null;

        try {
            saveOrder = orderHandler.saveOrder(order);
            updateInventoryStock(order, product);
            auditLogHandler.logAuditDetails(order, "order placement succeeded");
        } catch (Exception ex) {
            auditLogHandler.logAuditDetails(order, "order placement failed");
        }

        // retries 3
        // notificationHandler.sendOrderConfirmation(order);

        //paymentValidatorHandler.validatePayment(saveOrder);

        // productRecommendationHandler.getRecommendations();

        //getCustomerDetails();

        return saveOrder;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void getCustomerDetails() {
        System.out.println("Customer details fetched!!!");
    }

    // Call this method after placeAnOrder is successfully completed
    public void processOrder(Order order) {
        // Step 1: Place the order
        Order saveOrder = placeAnOrder(order);

        // Step 2: Send notification(non-transactional)
        notificationHandler.sendOrderConfirmation(order);
    }

    private void validateStockAvailability(Order order, Product product) {
        if (order.getQuantity() > product.getStockQuantity()) {
            throw new InsufficientStockException("Insufficient stock!");
        }
    }

    private void updateInventoryStock(Order order, Product product) {
        int availableStock = product.getStockQuantity() - order.getQuantity();
        product.setStockQuantity(availableStock);
        inventoryHandler.updateProductDetails(product);
    }
}
