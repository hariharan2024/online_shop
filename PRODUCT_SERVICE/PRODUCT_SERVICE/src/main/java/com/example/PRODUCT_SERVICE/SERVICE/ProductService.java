package com.example.PRODUCT_SERVICE.SERVICE;

import com.example.INVENTORY_SERVICE.MODEL.Inventory;
import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRODUCT_SERVICE.MODEL.Product;
import com.example.PRODUCT_SERVICE.MODEL.Users;
import com.example.PRODUCT_SERVICE.REPOSITY.ProductRepository;
import com.example.PRODUCT_SERVICE.REPOSITY.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for the category: " + category);
        }
        for (Product product : products) {
            // Fetch price
            try {
                String priceServiceUrl = "http://localhost:8081/api/prices/" + product.getId();
                Double price = restTemplate.getForObject(priceServiceUrl, Double.class);
                product.setPrice(price != null ? price : Double.NaN);
            } catch (Exception e) {
                System.err.println("Failed to fetch price for product ID " + product.getId() + ": " + e.getMessage());
            }

            try {
                String inventoryServiceUrl = "http://localhost:8082/api/inventory/" + product.getId();
                Integer inventory = restTemplate.getForObject(inventoryServiceUrl, Integer.class);
                product.setInventory(inventory != null ? inventory : -1);
            } catch (Exception e) {
                System.err.println("Failed to fetch inventory for product ID " + product.getId() + ": " + e.getMessage());
            }

        }
        return products;
    }


    // Add a new product and its price and inventory to the respective services
    public Product addProduct(Product product) {
        // Save product in Product DB
        Product savedProduct = productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProductId(savedProduct.getId());  // Assign saved product's ID to inventory
        inventory.setAvailableStock(product.getInventory());  // Set the inventory quantity

        // Create and send Price object
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

    // Update an existing product
    public Product updateProduct(Long id, Product product) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        product.setId(id);
        return productRepository.save(product);
    }

    // Delete product by ID
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(id);
    }

}