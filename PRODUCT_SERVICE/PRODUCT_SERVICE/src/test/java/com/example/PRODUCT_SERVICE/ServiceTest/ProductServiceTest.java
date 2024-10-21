package com.example.PRODUCT_SERVICE.ServiceTest;

import com.example.PRODUCT_SERVICE.MODEL.Product;
import com.example.PRODUCT_SERVICE.REPOSITY.ProductRepository;
import com.example.PRODUCT_SERVICE.SERVICE.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        product = new Product();
        product.setId(1L);
        product.setName("Product A");
        product.setCategory("Electronics");
    }

    @Test
    void testGetProductsByCategory() {
        // Arrange
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByCategory("Electronics")).thenReturn(products);
        when(restTemplate.getForObject("http://localhost:8081/api/prices/1", Double.class)).thenReturn(100.0);
        when(restTemplate.getForObject("http://localhost:8082/api/inventory/1", Integer.class)).thenReturn(10);

        // Act
        List<Product> result = productService.getProductsByCategory("Electronics");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getPrice());
        assertEquals(10, result.get(0).getInventory());

        verify(productRepository, times(1)).findByCategory("Electronics");
        verify(restTemplate, times(1)).getForObject("http://localhost:8081/api/prices/1", Double.class);
        verify(restTemplate, times(1)).getForObject("http://localhost:8082/api/inventory/1", Integer.class);
    }

    @Test
    void testAddProduct() {
        // Arrange
        when(productRepository.save(product)).thenReturn(product);

        // Act
        Product savedProduct = productService.addProduct(product);

        // Assert
        assertNotNull(savedProduct);
        assertEquals("Product A", savedProduct.getName());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(product)).thenReturn(product);

        // Act
        Product updatedProduct = productService.updateProduct(1L, product);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(1L, updatedProduct.getId());

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productService.updateProduct(1L, product));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found", exception.getReason());

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> productService.deleteProduct(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found", exception.getReason());

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}
