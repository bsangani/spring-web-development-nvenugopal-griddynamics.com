package com.gd.ecom.service;

import com.gd.ecom.entity.*;
import com.gd.ecom.exception.*;
import com.gd.ecom.mapper.CartItemMapper;
import com.gd.ecom.model.CartItemDto;
import com.gd.ecom.model.CartRequest;
import com.gd.ecom.repository.CartItemRepository;
import com.gd.ecom.repository.CartRepository;
import com.gd.ecom.repository.SessionRepositoryApp;
import com.gd.ecom.service.impl.CartServiceImpl;
import com.gd.ecom.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class CartServiceImplTest {
    @Mock
    private ProductServiceImpl productService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private SessionRepositoryApp sessionRepositoryApp;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private CartServiceImpl cartServiceImpl;

    private String sessionId;
    private CartRequest cartRequest;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        sessionId = "session123";
        cartRequest = new CartRequest(1L, 2);

        product = Product.builder()
                .id(1L)
                .title("Product 1")
                .price(BigDecimal.valueOf(100.00))
                .available(5)
                .build();

        cartItem = CartItem.builder()
                .productId(1L)
                .name("Product 1")
                .quantity(2)
                .subtotal(BigDecimal.valueOf(200.00))
                .build();

        cart = new Cart();
        cart.setSessionId(sessionId);
        cart.setCartItemList(new ArrayList<>());
        cart.getCartItemList().add(cartItem);
    }

    @Test
    @DisplayName("Save Cart Success")
    void testAddToCart_Success() throws InterruptedException {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(sessionRepositoryApp.findBySessionId(sessionId)).thenReturn(Optional.of(new SpringSession()));
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartServiceImpl.addToCart(cartRequest, sessionId);

        verify(productService, times(1)).getProductById(1L);
        verify(sessionRepositoryApp, times(1)).findBySessionId(sessionId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Save Cart when Product not found")
    void testAddToCart_ProductNotFound() {
        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            cartServiceImpl.addToCart(cartRequest, sessionId);
        });

        verify(productService, times(1)).getProductById(1L);
        verify(sessionRepositoryApp, times(0)).findBySessionId(sessionId);
    }

    @Test
    @DisplayName("Save Cart when Insufficient stock")
    void testAddToCart_InsufficientStock() {
        product.setAvailable(1);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> {
            cartServiceImpl.addToCart(cartRequest, sessionId);
        });

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("Update Cart Success")
    void testUpdateCartById_Success() {

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem1);

        Long response = cartServiceImpl.updateCartById(cartRequest, sessionId);

        assertEquals(1L , response);
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(productService, times(1)).getProductById(1L);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Get All Cart Items Success")
    void testGetAllCartItems_Success() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(cartItemMapper.toCartItemDto(any(CartItem.class))).thenReturn(new CartItemDto());

        List<CartItemDto> cartItems = cartServiceImpl.getAllCartItems(sessionId);

        assertFalse(cartItems.isEmpty());
        verify(cartRepository, times(1)).findBySessionId(sessionId);
    }

    @Test
    @DisplayName("Delete Cart By Id Success")
    void testDeleteCartItemById_Success() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Long response = cartServiceImpl.deleteCartItemById(1L, sessionId);

        assertEquals(1L, response);
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Delete Cart By Id Not Found")
    void testDeleteCartItemById_NotFound() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));

        assertThrows(ProductNotFoundException.class, () -> {
            cartServiceImpl.deleteCartItemById(999L, sessionId);
        });

        verify(cartRepository, times(1)).findBySessionId(sessionId);
    }
}

