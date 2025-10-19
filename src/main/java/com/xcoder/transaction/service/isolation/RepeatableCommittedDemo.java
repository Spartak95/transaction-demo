package com.xcoder.transaction.service.isolation;

import com.xcoder.transaction.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RepeatableCommittedDemo {
    private final ProductService productService;

    public RepeatableCommittedDemo(ProductService productService) {
        this.productService = productService;
    }

    public void demonstrateRepeatableRead(int id) throws InterruptedException {
        // Transaction A: Update the stock
        Thread transactionA = new Thread(() -> {
            try {
                productService.updateStock(id, 5);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        // Transaction B: Read the stock multiple times
        Thread transactionB = new Thread(() -> {
            try {
                productService.fetchStock(id); // Read stock before and after Transaction A's update
            } catch (Exception e) {
                log.error("Transaction B: Exception occurred: {}", e.getMessage());
            }
        });

        // Start both transactions
        transactionA.start();
        transactionB.start();

        // Wait for both threads to complete
        transactionA.join();
        transactionB.join();
    }
}
