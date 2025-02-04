package com.xcoder.transaction.repository;

import com.xcoder.transaction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Product, Integer> {
}
