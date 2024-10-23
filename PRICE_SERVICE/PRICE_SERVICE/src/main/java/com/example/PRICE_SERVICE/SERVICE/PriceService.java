package com.example.PRICE_SERVICE.SERVICE;


import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRICE_SERVICE.REPOSITORY.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public Price getPriceByProductId(Long productId) {
        return priceRepository.findByProductId(productId);
    }
    public void deletePrice(Long productId) {
        Price price = priceRepository.findByProductId(productId);
        if (price != null) {
            priceRepository.delete(price);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Price not found");
        }
    }


    public Price addPrice(Price price) {
        return priceRepository.save(price);
    }

    public Price updatePrice(Long productId, Price price) {
        Price existingPrice = priceRepository.findByProductId(productId);
        if (existingPrice == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Price not found for product ID: " + productId);
        }
        // Update the existing price with new values
        existingPrice.setPrice(price.getPrice()); // assuming you're updating the price
        return priceRepository.save(existingPrice);
    }



}
