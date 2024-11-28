package com.gd.ecom.service.impl;

import com.gd.ecom.entity.Product;
import com.gd.ecom.exception.ProductNotFoundException;
import com.gd.ecom.exception.ProductServiceException;
import com.gd.ecom.mapper.ProductMapper;
import com.gd.ecom.model.ProductRequest;
import com.gd.ecom.repository.ProductRepository;
import com.gd.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Long addProduct(ProductRequest productRequest) {
        log.info("Adding new product with title: {}", productRequest.getTitle());
        try {
            Product product = productMapper.changeProductRequestToProduct(productRequest);
            product = productRepository.save(product);
            log.info("Product added successfully with ID: {}", product.getId());
            return product.getId();
        } catch (Exception e) {
            log.error("Error occurred while adding product with title: {}", productRequest.getTitle(), e);
            throw new ProductServiceException("Error occurred while adding product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        if (productRepository.existsById(id)) {
            Optional<Product> product = productRepository.findById(id);
            if(product.isPresent()) {
                log.info("Product found with ID: {}", id);
                return product;
            } else {
                throw new ProductNotFoundException("Product not found with ID: "+id,HttpStatus.NOT_FOUND);
            }
        } else {
            log.warn("Product not found with ID: {}", id);
            return Optional.empty();
        }
    }

}
