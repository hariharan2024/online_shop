package com.example.INVENTORY_SERVICE;
import com.example.INVENTORY_SERVICE.CONTROLLER.InventoryController;
import com.example.INVENTORY_SERVICE.MODEL.Inventory;
import com.example.INVENTORY_SERVICE.SERVICE.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper; // for converting objects to JSON

    private Inventory inventory;

    @BeforeEach
    public void setup() {
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProductId(101L);
        inventory.setAvailableStock(50);
    }

    @Test
    public void testAddInventory() throws Exception {
        when(inventoryService.addInventory(inventory)).thenReturn(inventory);

        mockMvc.perform(post("/api/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(inventory.getId()))
                .andExpect(jsonPath("$.productId").value(inventory.getProductId()))
                .andExpect(jsonPath("$.quantity").value(inventory.getAvailableStock()));
    }

    @Test
    public void testGetInventoryByProductId() throws Exception {
        when(inventoryService.getInventoryByProductId(101L)).thenReturn(inventory);

        mockMvc.perform(get("/api/inventory/101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(101L));
    }

    @Test
    public void testUpdateInventory() throws Exception {
        inventory.setAvailableStock(100);
        when(inventoryService.updateInventory(inventory)).thenReturn(inventory);

        mockMvc.perform(post("/api/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(100));
    }

    @Test
    public void testDeleteInventory() throws Exception {
        doNothing().when(inventoryService).deleteInventory(101L);

        mockMvc.perform(delete("/api/inventory/delete/101"))
                .andExpect(status().isNoContent());
    }
}
