package com.xcoder.transaction.service.isolation;

import com.xcoder.transaction.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SerializableIsolationDemo {
    private final ProductService productService;

    public SerializableIsolationDemo(ProductService productService) {
        this.productService = productService;
    }

    public void testSerializableIsolation(int productId) throws InterruptedException {
        Thread transactionA = new Thread(() -> {
            try {
                productService.updateStock(productId, 5); // Update stock to 5
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        Thread transactionB = new Thread(() -> {
            try {
                Thread.sleep(1000); // Ensure Transaction A starts first
                int stock = productService.checkStock(productId); // Attempt to read the stock
                log.info("Transaction B: Final stock {}", stock);
            } catch (Exception e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        transactionA.start();
        transactionB.start();

        transactionA.join();
        transactionB.join();
    }
}
