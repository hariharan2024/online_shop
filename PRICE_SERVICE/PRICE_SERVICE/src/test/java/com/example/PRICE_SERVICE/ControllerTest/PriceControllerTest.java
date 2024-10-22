package com.example.PRICE_SERVICE.ControllerTest;

import com.example.PRICE_SERVICE.CONTROLLER.PriceController;
import com.example.PRICE_SERVICE.MODEL.Price;
import com.example.PRICE_SERVICE.SERVICE.PriceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
public class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceService priceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Price price;

    @BeforeEach
    public void setup() {
        price = new Price();
        price.setId(1L);
        price.setProductId(101L);
        price.setPrice(99.99);
    }

    @Test
    public void testGetPriceByProductId() throws Exception {
        when(priceService.getPriceByProductId(101L)).thenReturn(price);

        mockMvc.perform(get("/api/prices/101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(101L))
                .andExpect(jsonPath("$.amount").value(99.99));
    }

    @Test
    public void testAddPrice() throws Exception {
        when(priceService.addPrice(price)).thenReturn(price);

        mockMvc.perform(post("/api/prices/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(price)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(price.getId()))
                .andExpect(jsonPath("$.productId").value(price.getProductId()))
                .andExpect(jsonPath("$.amount").value(price.getPrice()));
    }
}
