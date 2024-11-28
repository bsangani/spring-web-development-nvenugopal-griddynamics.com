package com.gd.ecom.repository;


import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.gd.ecom.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Save Product")
    void testSaveProduct() {

        Product product = Product.builder()
                .title("Test Product")
                .available(10)
                .price(BigDecimal.valueOf(99.99))
                .build();

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();

        assertThat(savedProduct.getId()).isNotNull();

        assertThat(savedProduct.getTitle()).isEqualTo("Test Product");
        assertThat(savedProduct.getAvailable()).isEqualTo(10);
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(99.99));
    }

    @Test
    @DisplayName("Find Product by ID")
    void testFindProductById() {

        Product product = productRepository.save(Product.builder()
                .title("Test Product")
                .available(10)
                .price(BigDecimal.valueOf(99.99))
                .build());

        Optional<Product> foundProduct = productRepository.findById(product.getId());

        assertThat(foundProduct).isPresent();

        assertThat(foundProduct.get().getTitle()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Update Product")
    void testUpdateProduct() {

        Product product = productRepository.save(Product.builder()
                .title("Original Product")
                .available(5)
                .price(BigDecimal.valueOf(49.99))
                .build());

        product.setTitle("Updated Product");
        product.setAvailable(15);
        product.setPrice(BigDecimal.valueOf(59.99));

        Product updatedProduct = productRepository.save(product);

        assertThat(updatedProduct.getTitle()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getAvailable()).isEqualTo(15);
        assertThat(updatedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(59.99));
    }

    @Test
    @DisplayName("Delete Product")
    void testDeleteProduct() {

        Product product = productRepository.save(Product.builder()
                .title("Test Product")
                .available(10)
                .price(BigDecimal.valueOf(99.99))
                .build());

        productRepository.delete(product);

        Optional<Product> deletedProduct = productRepository.findById(product.getId());
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    @DisplayName("Find All Products")
    void testFindAllProducts() {

        productRepository.saveAll(List.of(
                Product.builder().title("Product 1").available(10).price(BigDecimal.valueOf(29.99)).build(),
                Product.builder().title("Product 2").available(20).price(BigDecimal.valueOf(49.99)).build(),
                Product.builder().title("Product 3").available(30).price(BigDecimal.valueOf(69.99)).build()
        ));

        List<Product> products = productRepository.findAll();

        assertThat(products).isNotEmpty();
        assertThat(products).hasSize(3);
    }
}





