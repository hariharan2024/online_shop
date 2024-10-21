package com.example.PRICE_SERVICE.CONTROLLER;


import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRICE_SERVICE.SERVICE.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @Autowired
    private PriceService priceService;

    @GetMapping("/{productId}")
    public Price getPriceByProductId(@PathVariable Long productId) {
        return priceService.getPriceByProductId(productId);
    }
}
