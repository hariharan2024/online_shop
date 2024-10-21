package com.example.PRICE_SERVICE.REPOSITORY;


import com.example.PRICE_SERVICE.MODEL.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    Price findByProductId(Long productId);
}
