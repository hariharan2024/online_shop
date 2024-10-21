package com.example.PRICE_SERVICE.SERVICE;


import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRICE_SERVICE.REPOSITORY.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public Price getPriceByProductId(Long productId) {
        return priceRepository.findByProductId(productId);
    }
}
