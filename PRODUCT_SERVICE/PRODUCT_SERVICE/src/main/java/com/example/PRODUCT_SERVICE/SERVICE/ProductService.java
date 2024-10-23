package com.example.PRODUCT_SERVICE.SERVICE;

import com.example.INVENTORY_SERVICE.MODEL.Inventory;
import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRODUCT_SERVICE.MODEL.Product;
import com.example.PRODUCT_SERVICE.REPOSITY.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private static final String INVENTORY_API_URL = "http://localhost:8082/api/inventory/add";
    private static final String PRICE_API_URL = "http://localhost:8081/api/prices/add";

    // Retry mechanism for external service calls
    @Retryable(
            value = {ResourceAccessException.class, HttpServerErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public Double getPrice(Long productId) {
        String priceServiceUrl = "http://localhost:8081/api/prices/" + productId;
        return restTemplate.getForObject(priceServiceUrl, Double.class);
    }

    @Retryable(
            value = {ResourceAccessException.class, HttpServerErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public Integer getInventory(Long productId) {
        String inventoryServiceUrl = "http://localhost:8082/api/inventory/" + productId;
        return restTemplate.getForObject(inventoryServiceUrl, Integer.class);
    }

    // Get products by category with stock check
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for the category: " + category);
        }

        for (Product product : products) {
            // Fetch price with retry mechanism
            try {
                Double price = getPrice(product.getId());
                product.setPrice(price != null ? price : Double.NaN);
            } catch (Exception e) {
                System.err.println("Failed to fetch price for product ID " + product.getId() + ": " + e.getMessage());
            }

            // Fetch inventory with retry mechanism and check for stock
            try {
                Integer inventory = getInventory(product.getId());
                if (inventory != null && inventory <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + product.getId() + " has no stock available.");
                }
                product.setInventory(inventory != null ? inventory : -1);
            } catch (Exception e) {
                System.err.println("Failed to fetch inventory for product ID " + product.getId() + ": " + e.getMessage());
            }
        }
        return products;
    }

    // Add a new product and its price and inventory to respective services
    public Product addProduct(Product product) {
        // Save product in Product DB
        Product savedProduct = productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProductId(savedProduct.getId());  // Assign saved product's ID to inventory
        inventory.setAvailableStock(product.getInventory());  // Set the inventory quantity

        Price price = new Price();
        price.setProductId(savedProduct.getId());  // Assign saved product's ID to price
        price.setPrice(product.getPrice());  // Set the price

        // Call Inventory Service to add inventory data
        try {
            ResponseEntity<Inventory> response = restTemplate.postForEntity(INVENTORY_API_URL, inventory, Inventory.class);
            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add inventory");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Inventory service unavailable", e);
        }

        // Call Price Service to add price data
        try {
            ResponseEntity<Price> priceResponse = restTemplate.postForEntity(PRICE_API_URL, price, Price.class);
            if (priceResponse.getStatusCode() != HttpStatus.CREATED) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add price");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Price service unavailable", e);
        }

        return savedProduct;
    }

    public Product updateProduct(Long id, Product product) {
        // Check if the product exists
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // Update product details
        existingProduct.setPrice(product.getPrice());
        existingProduct.setInventory(product.getInventory());
        Product updatedProduct = productRepository.save(existingProduct);

        // Update Inventory Service
        try {
            String inventoryServiceUrl = "http://localhost:8082/api/inventory/update/" + id;
            Inventory inventory = new Inventory();
            inventory.setProductId(updatedProduct.getId());
            inventory.setAvailableStock(updatedProduct.getInventory()); // Use updated inventory value
            restTemplate.put(inventoryServiceUrl, inventory); // Use PUT for updates
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Inventory service unavailable", e);
        }

        // Update Price Service
        try {
            String priceServiceUrl = "http://localhost:8081/api/prices/update/" + id;
            Price price = new Price();
            price.setProductId(updatedProduct.getId());
            price.setPrice(updatedProduct.getPrice()); // Use updated price value
            restTemplate.put(priceServiceUrl, price); // Use PUT for updates
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Price service unavailable", e);
        }

        return updatedProduct;
    }


    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        // Delete from Inventory Service
        try {
            String inventoryServiceUrl = "http://localhost:8082/api/inventory/delete/" + id;
            restTemplate.delete(inventoryServiceUrl);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Inventory service unavailable", e);
        }

        // Delete from Price Service
        try {
            String priceServiceUrl = "http://localhost:8081/api/prices/delete/" + id;
            restTemplate.delete(priceServiceUrl);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Price service unavailable", e);
        }

        // Finally, delete the product from Product DB
        productRepository.deleteById(id);
    }



}
