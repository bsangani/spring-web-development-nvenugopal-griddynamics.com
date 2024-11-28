package com.gd.ecom.service.impl;

import com.gd.ecom.entity.*;
import com.gd.ecom.exception.EmptyCartException;
import com.gd.ecom.exception.ProductNotFoundException;
import com.gd.ecom.mapper.CartItemMapper;
import com.gd.ecom.repository.*;
import com.gd.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public Long checkOutOrder(String sessionId) {

        log.info("Checking out order for session ID: {}", sessionId);
        Optional<Cart> cartOpt = cartRepository.findBySessionId(sessionId);
        Cart cart = cartOpt.orElseThrow(() -> new EmptyCartException("Cart not found for the session.", HttpStatus.NOT_FOUND));
        List<CartItem> cartItems = cart.getCartItemList();
        if (cartItems.isEmpty()) {
            log.warn("No items found in cart to check out for session ID: {}", sessionId);
            throw new EmptyCartException("No items found in cart to check out.", HttpStatus.NOT_FOUND);
        }

        String email = getLoginUserEmail();
        log.info("User email obtained from security context: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));

        Set<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Long productId = cartItem.getProductId();
            Product product = productRepository.findWithLockingById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found for ID: " + productId, HttpStatus.NOT_FOUND));
            if (product.getAvailable() < cartItem.getQuantity()) {
                log.error("Product not available. Product ID: {}, Available Quantity: {}, Required Quantity: {}", productId, product.getAvailable(), cartItem.getQuantity());
                throw new ProductNotFoundException("Product not available. Available Quantity: " + product.getAvailable() + " Required: " + cartItem.getQuantity() + " Check availability after sometime", HttpStatus.NOT_FOUND);
            }
            product.setAvailable(product.getAvailable() - cartItem.getQuantity());
            productRepository.saveAndFlush(product);
            OrderItem orderItem = cartItemMapper.toOrderItem(cartItem);
            orderItem.setProduct(product);
            return orderItem;
        }).collect(Collectors.toSet());

        CustomerOrder customerOrder = CustomerOrder.builder()
                .orderDate(LocalDateTime.now())
                .user(user)
                .orderAmount(calculateOrderAmount(orderItems))
                .orderItems(orderItems)
                .build();

        CustomerOrder order = customerOrderRepository.save(customerOrder);
        log.info("Order placed successfully with ID: {}", order.getId());

        cart.getCartItemList().clear();
        cartRepository.saveAndFlush(cart);
        log.info("Cart cleared for session ID: {}", sessionId);

        return order.getId();
    }

    private BigDecimal calculateOrderAmount(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getLoginUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
