package com.gd.ecom.service.impl;

import com.gd.ecom.entity.Cart;
import com.gd.ecom.entity.CartItem;
import com.gd.ecom.entity.Product;
import com.gd.ecom.entity.SpringSession;
import com.gd.ecom.exception.EmptyCartException;
import com.gd.ecom.exception.InsufficientStockException;
import com.gd.ecom.exception.ProductNotFoundException;
import com.gd.ecom.exception.SessionNotFoundException;
import com.gd.ecom.mapper.CartItemMapper;
import com.gd.ecom.model.CartItemDto;
import com.gd.ecom.model.CartRequest;
import com.gd.ecom.repository.CartItemRepository;
import com.gd.ecom.repository.CartRepository;
import com.gd.ecom.repository.SessionRepositoryApp;
import com.gd.ecom.service.CartService;
import com.gd.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final SessionRepositoryApp sessionRepositoryApp;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public void addToCart(CartRequest cartRequest, String sessionId) {

        log.info("Adding product to cart. Session ID: {}, Product ID: {}", sessionId, cartRequest.getId());
        Product product = getProductOrThrowNotFound(cartRequest.getId());
        checkStockAndThrowIfInsufficient(product, cartRequest.getQuantity());

        Cart cart = getOrCreateCart(sessionId);
        BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity()));
        CartItem cartItem = createCartItem(product, cartRequest.getQuantity(), itemSubtotal, cart);
        cart.getCartItemList().add(cartItem);
        cartRepository.save(cart);
        log.info("Product added to cart successfully. Session ID: {}, Product ID: {}", sessionId, cartRequest.getId());
    }

    @Override
    public Long updateCartById(CartRequest cartRequest, String sessionId) {
        log.info("Updating cart. Session ID: {}, Product ID: {}", sessionId, cartRequest.getId());
        Cart cart = getCartOrThrowIfEmpty(sessionId);
        CartItem cartItem = getCartItemByIdOrThrowNotFound(cartRequest.getId(), cart);

        Product product = getProductOrThrowNotFound(cartRequest.getId());
        checkStockAndThrowIfInsufficient(product, cartRequest.getQuantity());

        cartItem.setQuantity(cartRequest.getQuantity());
        cartItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity())));
        var response = cartItemRepository.save(cartItem);
        log.info("Cart updated successfully. Session ID: {}, Product ID: {}", sessionId, cartRequest.getId());
        return response.getId();
    }

    @Override
    public List<CartItemDto> getAllCartItems(String sessionId) {
        log.info("Fetching all cart items. Session ID: {}", sessionId);
        Cart cart = getCartOrThrowIfEmpty(sessionId);
        List<CartItemDto> cartItems = cart.getCartItemList().stream()
                .map(cartItemMapper::toCartItemDto)
                .collect(Collectors.toList());
        log.info("Fetched all cart items successfully. Session ID: {}", sessionId);
        return cartItems;
    }

    @Override
    public Long deleteCartItemById(Long id, String sessionId) {
        log.info("Deleting cart item. Session ID: {}, Product ID: {}", sessionId, id);
        Cart cart = getCartOrThrowIfEmpty(sessionId);
        CartItem cartItem = getCartItemByIdOrThrowNotFound(id, cart);
        cart.getCartItemList().remove(cartItem);
        cartRepository.save(cart);
        log.info("Cart item deleted successfully. Session ID: {}, Product ID: {}", sessionId, id);
        return id;
    }

    private Product getProductOrThrowNotFound(Long productId) {
        log.debug("Fetching product by ID: {}", productId);
        return productService.getProductById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found. ID: {}", productId);
                    return new ProductNotFoundException("Requested product with ID " + productId + " not found", HttpStatus.NOT_FOUND);
                });
    }

    private void checkStockAndThrowIfInsufficient(Product product, int quantity) {
        if (quantity > product.getAvailable()) {
            log.error("Insufficient stock. Product ID: {}, Requested Quantity: {}, Available: {}", product.getId(), quantity, product.getAvailable());
            throw new InsufficientStockException("Requested quantity exceeds available stock for product: " + product.getTitle() +
                    ". Available quantity: " + product.getAvailable(), HttpStatus.BAD_REQUEST);
        }
    }

    private Cart getCartOrThrowIfEmpty(String sessionId) {
        log.debug("Fetching cart by session ID: {}", sessionId);
        return cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> {
                    log.warn("Cart is empty. Session ID: {}", sessionId);
                    return new EmptyCartException("Your Cart is Empty Add Some Product to cart.", HttpStatus.NOT_FOUND);
                });
    }

    private CartItem getCartItemByIdOrThrowNotFound(Long id, Cart cart) {
        log.debug("Fetching cart item by ID: {} from cart", id);
        return cart.getCartItemList().stream()
                .filter(cartItem -> cartItem.getProductId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Cart item not found. Product ID: {}", id);
                    return new ProductNotFoundException("No Orders found for the id " + id, HttpStatus.NOT_FOUND);
                });
    }

    private CartItem createCartItem(Product product, int quantity, BigDecimal itemSubtotal, Cart cart) {
        log.debug("Creating cart item. Product ID: {}, Quantity: {}", product.getId(), quantity);
        return CartItem.builder()
                .productId(product.getId())
                .name(product.getTitle())
                .quantity(quantity)
                .subtotal(itemSubtotal)
                .cart(cart)
                .build();
    }

    private Cart getOrCreateCart(String sessionId) {
        log.debug("Getting or creating cart for session ID: {}", sessionId);
        SpringSession session = sessionRepositoryApp.findBySessionId(sessionId)
                .orElseThrow(() -> {
                    log.error("Session not found. Session ID: {}", sessionId);
                    return new SessionNotFoundException("Session not found", HttpStatus.NOT_FOUND);
                });

        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    log.info("Creating new cart for session ID: {}", sessionId);
                    return createNewCart(sessionId, session);
                });
    }

    private Cart createNewCart(String sessionId, SpringSession session) {
        log.debug("Creating new cart for session ID: {}", sessionId);
        Cart cart = new Cart();
        cart.setName("Cart for session " + sessionId);
        cart.setSessionId(sessionId);
        cart.setId(session.getPrimaryId());
        log.info("New cart created for session ID: {}", sessionId);
        return cart;
    }
}
