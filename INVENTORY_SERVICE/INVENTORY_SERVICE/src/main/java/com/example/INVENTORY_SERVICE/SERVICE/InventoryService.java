package com.example.INVENTORY_SERVICE.SERVICE;


import com.example.INVENTORY_SERVICE.MODEL.Inventory;
import com.example.INVENTORY_SERVICE.REPOSITORY.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public void deleteInventory(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (inventory != null) {
            inventoryRepository.delete(inventory);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found");
        }
    }

    public Inventory addInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }


    public Inventory updateInventory(Inventory inventory) {
        Inventory existingInventory = inventoryRepository.findByProductId(inventory.getProductId());
        if (existingInventory == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found for product ID: " + inventory.getProductId());
        }
        // Update the existing inventory with new values
        existingInventory.setAvailableStock(inventory.getAvailableStock());
        return inventoryRepository.save(existingInventory);
    }


}
