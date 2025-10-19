package com.xcoder.transaction.service;

import com.xcoder.transaction.entity.Product;
import com.xcoder.transaction.exception.ProductNotFoundException;
import com.xcoder.transaction.repository.InventoryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final EntityManager entityManager;
    private final InventoryRepository inventoryRepository;

    // Transaction A
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateStock(int productId, int stock) throws InterruptedException {
        // Retrieve the product and update its stock
        Product product = inventoryRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setStockQuantity(stock);

        inventoryRepository.save(product);
        entityManager.flush(); // Ensure the update is sent to the DB

        // Simulate a long-running transaction(does not commit yet)
        log.info("Transaction A: Stock updated to {}", stock);
        Thread.sleep(5000);

        //log.info("Transaction A: Rolling back the update");
        //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        log.info("Transaction A: Commited the update");
    }

    // Transaction B: Read stock
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int checkStock(int productId) {
        // Retrieve the product and read its stock (potentially dirty read)
        Product product = inventoryRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        log.info("Transaction B: Read stock as {}", product.getStockQuantity());

        return product.getStockQuantity();
    }
}
