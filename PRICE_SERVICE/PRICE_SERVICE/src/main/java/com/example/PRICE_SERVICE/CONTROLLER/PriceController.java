package com.example.PRICE_SERVICE.CONTROLLER;


import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRICE_SERVICE.SERVICE.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @Autowired
    private PriceService priceService;

    // Get price by product ID
    @GetMapping("/{productId}")
    public Price getPriceByProductId(@PathVariable Long productId) {
        return priceService.getPriceByProductId(productId);
    }

    // Update an existing price
    @PutMapping("update/{productId}")
    public ResponseEntity<Price> updatePrice(@PathVariable Long productId, @RequestBody Price price) {
        Price updatedPrice = priceService.updatePrice(productId, price);
        return ResponseEntity.ok(updatedPrice);
    }

    // Delete price by product ID
    @DeleteMapping("delete/{productId}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long productId) {
        priceService.deletePrice(productId);
        return ResponseEntity.noContent().build();
    }
    // Add a new price for a product
    @PostMapping("/add")
    public ResponseEntity<Price> addPrice(@RequestBody Price price) {
        Price createdPrice = priceService.addPrice(price);  // Changed createdInventory to createdPrice
        return new ResponseEntity<>(createdPrice, HttpStatus.CREATED);
    }
}
