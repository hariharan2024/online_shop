package com.example.PRODUCT_SERVICE.REPOSITY;

import com.example.PRODUCT_SERVICE.MODEL.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    // Custom query to find products by price and inventory
    @Query("SELECT p FROM Product p WHERE p.price <= :maxPrice AND p.inventory >= :minInventory")
    List<Product> findByPriceAndInventory(Double maxPrice, Integer minInventory);

}
