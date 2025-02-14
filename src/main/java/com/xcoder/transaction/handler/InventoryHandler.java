package com.xcoder.transaction.handler;

import com.xcoder.transaction.entity.Product;
import com.xcoder.transaction.exception.DatabaseCrashException;
import com.xcoder.transaction.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryHandler {
    private final InventoryRepository inventoryRepository;

    public InventoryHandler(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateProductDetails(Product product) {
        // forcefully throwing exception to simulate use of tx
        if (product.getPrice() > 5000) {
            throw new DatabaseCrashException("DB crashed...");
        }
        inventoryRepository.save(product);
    }

    public Product getProduct(int id) {
        return inventoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not available with id: " + id));
    }
}
