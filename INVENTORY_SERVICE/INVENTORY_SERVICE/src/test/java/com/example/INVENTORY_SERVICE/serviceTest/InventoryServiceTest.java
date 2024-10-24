package com.example.INVENTORY_SERVICE.serviceTest;



import com.example.INVENTORY_SERVICE.MODEL.Inventory;
import com.example.INVENTORY_SERVICE.REPOSITORY.InventoryRepository;
import com.example.INVENTORY_SERVICE.SERVICE.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProductId(101L);
        inventory.setAvailableStock(10);
    }

    @Test
    void testGetInventoryByProductId() {
        // Arrange
        when(inventoryRepository.findByProductId(101L)).thenReturn(inventory);

        // Act
        Inventory foundInventory = inventoryService.getInventoryByProductId(101L);

        // Assert
        assertNotNull(foundInventory);
        assertEquals(101L, foundInventory.getProductId());
        verify(inventoryRepository, times(1)).findByProductId(101L);
    }

  //  @Test
    void testUpdateInventory() {
        // Arrange
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        // Act
        Inventory updatedInventory = inventoryService.updateInventory(inventory);

        // Assert
        assertNotNull(updatedInventory);
        assertEquals(101L, updatedInventory.getProductId());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void testDeleteInventory_WhenExists() {
        // Arrange
        when(inventoryRepository.findByProductId(101L)).thenReturn(inventory);
        doNothing().when(inventoryRepository).delete(inventory);

        // Act
        assertDoesNotThrow(() -> inventoryService.deleteInventory(101L));

        // Assert
        verify(inventoryRepository, times(1)).findByProductId(101L);
        verify(inventoryRepository, times(1)).delete(inventory);
    }

    @Test
    void testDeleteInventory_WhenNotFound() {
        // Arrange
        when(inventoryRepository.findByProductId(101L)).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> inventoryService.deleteInventory(101L));
        assertEquals("404 NOT_FOUND \"Inventory not found\"", exception.getMessage());

        verify(inventoryRepository, times(1)).findByProductId(101L);
        verify(inventoryRepository, never()).delete(any());
    }
}

