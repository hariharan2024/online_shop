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

    @GetMapping("/{productId}")
    public Price getPriceByProductId(@PathVariable Long productId) {
        return priceService.getPriceByProductId(productId);
    }
    @PostMapping("/add")
    public ResponseEntity<Price> addPrice(@RequestBody Price price) {
        Price createdInventory =priceService.addPrice(price);
        return new ResponseEntity<>(createdInventory, HttpStatus.CREATED);
    }
}
