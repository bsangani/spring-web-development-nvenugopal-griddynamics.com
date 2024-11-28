package com.gd.ecom.service;

import com.gd.ecom.entity.Product;
import com.gd.ecom.model.ProductRequest;

import java.util.Optional;

public interface ProductService {
    Long addProduct(ProductRequest productRequest);
    Optional<Product> getProductById(Long id);
}
