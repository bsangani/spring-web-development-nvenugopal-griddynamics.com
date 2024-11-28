package com.gd.ecom.repository;

import com.gd.ecom.entity.Cart;
import com.gd.ecom.entity.CartItem;
import com.gd.ecom.entity.Product;
import com.gd.ecom.entity.SpringSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private SessionRepositoryApp sessionRepositoryApp;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Test Saving and Finding Cart by Session ID")
    void testSaveAndFindCartBySessionId() {

        SpringSession session = createAndSaveSession();
        Product product = createAndSaveProduct();
        Cart cart = createCart(session);
        CartItem cartItem = createAndSaveCartItem(cart, product);

        cart.getCartItemList().add(cartItem);
        Cart savedCart = cartRepository.save(cart);


        assertCart(savedCart, session.getSessionId(), product);
    }

    private SpringSession createAndSaveSession() {
        SpringSession session = SpringSession.builder()
                .sessionId("123451213")
                .primaryId("123456789")
                .creationTime(213123131L)
                .expiryTime(123213132123L)
                .build();
        return sessionRepositoryApp.save(session);
    }

    private Product createAndSaveProduct() {
        Product product = new Product();
        product.setTitle("Test Product");
        product.setPrice(BigDecimal.valueOf(10.00));
        product.setAvailable(100);
        return productRepository.save(product);
    }

    private Cart createCart(SpringSession session) {
        Cart cart = new Cart();
        cart.setSessionId(session.getSessionId());
        cart.setId(session.getPrimaryId());
        cart.setName("Test Cart");
        return cart;
    }

    private CartItem createAndSaveCartItem(Cart cart, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(product.getId());
        cartItem.setName(product.getTitle());
        cartItem.setQuantity(1);
        cartItem.setSubtotal(product.getPrice());
        cartItem.setCart(cart);
        return cartItemRepository.save(cartItem);
    }

    private void assertCart(Cart savedCart, String sessionId, Product product) {
        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getSessionId()).isEqualTo(sessionId);
        assertThat(savedCart.getName()).isEqualTo("Test Cart");
        assertThat(savedCart.getCartItemList()).hasSize(1);

        CartItem savedCartItem = savedCart.getCartItemList().get(0);
        assertThat(savedCartItem.getProductId()).isEqualTo(product.getId());
        assertThat(savedCartItem.getName()).isEqualTo(product.getTitle());
        assertThat(savedCartItem.getQuantity()).isEqualTo(1);
        assertThat(savedCartItem.getSubtotal()).isEqualTo(product.getPrice());
    }
}
