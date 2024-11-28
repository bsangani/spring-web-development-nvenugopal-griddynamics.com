package com.gd.ecom.repository;


import com.gd.ecom.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,String> {
    Optional<Cart> findBySessionId(String id);
}
