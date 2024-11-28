package com.gd.ecom.service;

import com.gd.ecom.model.CartItemDto;
import com.gd.ecom.model.CartRequest;

import java.util.List;

public interface CartService {

    void addToCart(CartRequest cartRequest, String sessionId);

    Long updateCartById(CartRequest cartRequest, String id);

    List<CartItemDto> getAllCartItems(String sessionId);

    Long deleteCartItemById(Long id, String sessionId);
}
