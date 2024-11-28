package com.gd.ecom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gd.ecom.model.CartItemDto;
import com.gd.ecom.model.CartRequest;
import com.gd.ecom.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    @DisplayName("Add item to cart - Success")
    void testAddToCart() throws Exception {

        CartRequest cartRequest = new CartRequest(10L, 10);
        String jsonRequest = objectMapper.writeValueAsString(cartRequest);

        doNothing().when(cartService).addToCart(any(CartRequest.class), any(String.class));

        mockMvc.perform(post("/api/user/addToCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(cartService).addToCart(any(CartRequest.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    @DisplayName("Update cart item - Success")
    void testUpdateCartInformation() throws Exception {
        CartRequest cartRequest = new CartRequest(10L, 10);
        String jsonRequest = objectMapper.writeValueAsString(cartRequest);

       when(cartService.updateCartById(any(CartRequest.class), any(String.class))).thenReturn(10L);

        mockMvc.perform(put("/api/user/updateCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(cartService).updateCartById(any(CartRequest.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    @DisplayName("Get all cart items - Success")
    void testGetCartItems() throws Exception {
        CartItemDto cartItemDto = new CartItemDto(1L, "softdrink", BigDecimal.TEN, 10, 1L);
        when(cartService.getAllCartItems(any(String.class))).thenReturn(Collections.singletonList(cartItemDto));

        mockMvc.perform(get("/api/user/getCartItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].quantity").exists())
                .andExpect(jsonPath("$[0].subtotal").exists());

        verify(cartService).getAllCartItems(any(String.class));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    @DisplayName("Delete cart item by ID - Success")
    void testDeleteCartItemById() throws Exception {
        Long itemId = 123L;
        when(cartService.deleteCartItemById(any(Long.class), any(String.class))).thenReturn(itemId);
        mockMvc.perform(delete("/api/user/cart/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        verify(cartService, times(1)).deleteCartItemById(any(Long.class), any(String.class));
    }
}
