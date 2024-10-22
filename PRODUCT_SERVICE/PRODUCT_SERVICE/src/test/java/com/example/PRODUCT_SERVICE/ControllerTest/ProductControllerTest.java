package com.example.PRODUCT_SERVICE.ControllerTest;

import com.example.PRODUCT_SERVICE.CONTROLLER.ProductController;
import com.example.PRODUCT_SERVICE.MODEL.Product;
import com.example.PRODUCT_SERVICE.SERVICE.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    public void setup() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setCategory("Electronics");
        product.setPrice(299.99);
    }

    @Test
    public void testGetProductsByCategory() throws Exception {
        List<Product> productList = Arrays.asList(product);
        when(productService.getProductsByCategory("Electronics")).thenReturn(productList);

        mockMvc.perform(get("/api/products/category/Electronics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    public void testAddProduct() throws Exception {
        when(productService.addProduct(product)).thenReturn(product);

        mockMvc.perform(post("/api/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.price").value(299.99));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        product.setPrice(399.99);
        when(productService.updateProduct(1L, product)).thenReturn(product);

        mockMvc.perform(put("/api/products/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.price").value(399.99));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

