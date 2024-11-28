package com.gd.ecom.repository;

import com.gd.ecom.entity.CustomerOrder;
import com.gd.ecom.entity.OrderItem;
import com.gd.ecom.entity.Product;
import com.gd.ecom.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerOrderRepositoryTest {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private User savedUser;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        savedUser = saveUser();
        savedProduct = saveProduct();
    }

    @Test
    @DisplayName("Test Saving a Customer Order")
    void testSaveCustomerOrder() {
        CustomerOrder customerOrder = createCustomerOrder(savedUser, savedProduct, BigDecimal.valueOf(100), "Product A", BigDecimal.valueOf(50), 2);

        CustomerOrder savedCustomerOrder = customerOrderRepository.save(customerOrder);

        assertThat(savedCustomerOrder).isNotNull();
        assertThat(savedCustomerOrder.getId()).isNotNull();

        Optional<CustomerOrder> retrievedCustomerOrder = customerOrderRepository.findById(savedCustomerOrder.getId());

        assertThat(retrievedCustomerOrder).isPresent();
        assertThat(retrievedCustomerOrder.get().getOrderAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Test Finding All Customer Orders")
    void testFindAllCustomerOrders() {
        customerOrderRepository.saveAll(createCustomerOrders(savedUser, savedProduct));

        Iterable<CustomerOrder> allCustomerOrders = customerOrderRepository.findAll();

        assertThat(allCustomerOrders).isNotEmpty();
    }

    @Test
    @DisplayName("Test Deleting a Customer Order")
    void testDeleteCustomerOrder() {
        CustomerOrder customerOrder = customerOrderRepository.save(createCustomerOrders(savedUser, savedProduct).get(0));

        customerOrderRepository.delete(customerOrder);

        Optional<CustomerOrder> deletedCustomerOrder = customerOrderRepository.findById(customerOrder.getId());
        assertFalse(deletedCustomerOrder.isPresent());
    }

    private User saveUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        return userRepository.save(user);
    }

    private Product saveProduct() {
        Product product = new Product();
        product.setAvailable(10);
        product.setPrice(BigDecimal.valueOf(1000));
        product.setTitle("Some product");
        return productRepository.save(product);
    }

    private CustomerOrder createCustomerOrder(User user, Product product, BigDecimal orderAmount, String productName, BigDecimal subtotal, int quantity) {
        OrderItem orderItem = OrderItem.builder()
                .name(productName)
                .subtotal(subtotal)
                .quantity(quantity)
                .product(product)
                .build();

        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem);

        return CustomerOrder.builder()
                .orderDate(LocalDateTime.now())
                .orderAmount(orderAmount)
                .orderItems(orderItems)
                .user(user)
                .build();
    }

    private List<CustomerOrder> createCustomerOrders(User user, Product product) {
        List<CustomerOrder> customerOrders = new ArrayList<>();

        customerOrders.add(createCustomerOrder(user, product, BigDecimal.valueOf(100), "Product A", BigDecimal.valueOf(50), 2));
        customerOrders.add(createCustomerOrder(user, product, BigDecimal.valueOf(150), "Product B", BigDecimal.valueOf(80), 1));

        return customerOrders;
    }
}
