package com.xcoder.transaction.service.isolation;

import com.xcoder.transaction.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReadUncommittedDemo {
    private final ProductService productService;

    public ReadUncommittedDemo(ProductService productService) {
        this.productService = productService;
    }

    public void testReadUncommitted(int id) throws InterruptedException {
        // Start Transaction A(Thread 1) to update the stock but not commit, then roll back
        Thread threadA = new Thread(() -> {
            try {
                productService.updateStock(id, 5);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        // Start Transaction B(Thread 2) to read the stock
        // 50
        Thread threadB = new Thread(() -> {
            try {
                Thread.sleep(2000); // Wait a moment to ensure Thread A starts and holds the transaction
                int stock = productService.checkStock(id); // Read stock during Transaction A
                log.info("Stock read by Transaction B: {}", stock);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        // Start the threads
        threadA.start();
        threadB.start();

        // Wait for threads to complete
        threadA.join();
        threadB.join();
    }
}
