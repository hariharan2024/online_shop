package com.example.PRICE_SERVICE.ServiceTest;
import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRICE_SERVICE.REPOSITORY.PriceRepository;
import com.example.PRICE_SERVICE.SERVICE.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    private Price price;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        price = new Price();
        price.setId(1L);
        price.setProductId(101L);
        price.setPrice(200.50);
    }

    @Test
    void testGetPriceByProductId() {
        // Arrange
        when(priceRepository.findByProductId(101L)).thenReturn(price);

        // Act
        Price foundPrice = priceService.getPriceByProductId(101L);

        // Assert
        assertNotNull(foundPrice);
        assertEquals(101L, foundPrice.getProductId());
        assertEquals(200.50, foundPrice.getAmount());
        verify(priceRepository, times(1)).findByProductId(101L);
    }

    @Test
    void testGetPriceByProductId_NotFound() {
        // Arrange
        when(priceRepository.findByProductId(101L)).thenReturn(null);

        // Act
        Price foundPrice = priceService.getPriceByProductId(101L);

        // Assert
        assertNull(foundPrice);
        verify(priceRepository, times(1)).findByProductId(101L);
    }
}

