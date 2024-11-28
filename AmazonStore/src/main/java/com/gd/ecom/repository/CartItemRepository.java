package com.gd.ecom.repository;

import com.gd.ecom.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    void deleteByProductId(Long id);
}
