package com.gd.ecom.service;

import com.gd.ecom.entity.Product;
import com.gd.ecom.exception.ProductServiceException;
import com.gd.ecom.mapper.ProductMapper;
import com.gd.ecom.model.ProductRequest;
import com.gd.ecom.repository.ProductRepository;
import com.gd.ecom.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductServiceImpl productService;
    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .title("Product 1")
                .price(BigDecimal.valueOf(100.00))
                .build();

        product = Product.builder()
                .id(1L)
                .title("Product 1")
                .price(BigDecimal.valueOf(100.00))
                .build();
    }

    @Test
    @DisplayName("Save Product Success")
    void testAddProduct_Success() {
        when(productMapper.changeProductRequestToProduct(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        Long productId = productService.addProduct(productRequest);

        assertNotNull(productId);
        assertEquals(1L, productId);
        verify(productMapper, times(1)).changeProductRequestToProduct(productRequest);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Save product Exception")
    void testAddProduct_Exception() {
        when(productMapper.changeProductRequestToProduct(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenThrow(new RuntimeException("Database error"));

        ProductServiceException exception = assertThrows(ProductServiceException.class, () -> {
            productService.addProduct(productRequest);
        });

        assertEquals("Error occurred while adding product", exception.getMessage());
        verify(productMapper, times(1)).changeProductRequestToProduct(productRequest);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Display product By Id Success")
    void testGetProductById_ProductExists()  {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.getReferenceById(1L)).thenReturn(product);

        Optional<Product> fetchedProduct = productService.getProductById(1L);

        assertTrue(fetchedProduct.isPresent());
        assertEquals(product.getId(), fetchedProduct.get().getId());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).getReferenceById(1L);
    }

    @Test
    @DisplayName("Get Product while Product not found")
    void testGetProductById_ProductDoesNotExist()  {
        when(productRepository.existsById(1L)).thenReturn(false);

        Optional<Product> fetchedProduct = productService.getProductById(1L);

        assertFalse(fetchedProduct.isPresent());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(0)).getReferenceById(anyLong());
    }
}

