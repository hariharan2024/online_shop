package com.example.PRODUCT_SERVICE.SERVICE;

import com.example.INVENTORY_SERVICE.MODEL.Inventory;
import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRODUCT_SERVICE.MODEL.Product;
import com.example.PRODUCT_SERVICE.REPOSITY.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // URL of the Inventory microservice
    private static final String INVENTORY_API_URL = "http://localhost:8082/api/inventory/add";
    private static final String PRICE_API_URL = "http://localhost:8081/api/price/add";


    public List<Product> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        for (Product product : products) {
            // Fetch product price from price service
            String priceServiceUrl = "http://localhost:8081/api/prices/" + product.getId();
            Double price = restTemplate.getForObject(priceServiceUrl, Double.class);
            product.setPrice(price != null ? price : Double.NaN); // If no price, set NaN

            // Fetch inventory from inventory service
            String inventoryServiceUrl = "http://localhost:8082/api/inventory/" + product.getId();
            Integer inventory = restTemplate.getForObject(inventoryServiceUrl, Integer.class);
            product.setInventory(inventory != null ? inventory : -1); // If no inventory, set -1
        }
        return products;
    }

    // Add new product and its inventory to both services
    public Product addProduct(Product product) {
        // Save product in Product DB
        Product savedProduct = productRepository.save(product);

        // Create inventory object
        Inventory inventory = new Inventory();
        Price price=new Price();
        inventory.setProductId(savedProduct.getId()); // Assign saved product's ID to inventory
        inventory.setAvailableStock(savedProduct.getInventory()); // Example quantity, consider making this dynamic
        price.setProductId(savedProduct.getId());
        price.setPrice(savedProduct.getPrice());
        // Call InventoryService to add inventory data
        try {
            ResponseEntity<Inventory> response = restTemplate.postForEntity(INVENTORY_API_URL, inventory, Inventory.class);
            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add inventory");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Inventory service unavailable", e);
        }
        try {
            ResponseEntity<Price> response = restTemplate.postForEntity(PRICE_API_URL,price, Price.class);
            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add Price");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Price service unavailable", e);
        }

        return savedProduct;
    }
    // Update existing product
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
