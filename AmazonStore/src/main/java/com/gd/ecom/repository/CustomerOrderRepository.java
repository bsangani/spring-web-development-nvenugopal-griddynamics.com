package com.gd.ecom.repository;

import com.gd.ecom.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

}
