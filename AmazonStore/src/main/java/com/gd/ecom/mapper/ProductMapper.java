package com.gd.ecom.mapper;

import com.gd.ecom.entity.Product;
import com.gd.ecom.model.ProductRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product changeProductRequestToProduct(ProductRequest productRequest);
}
