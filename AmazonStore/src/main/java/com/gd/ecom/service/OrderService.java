package com.gd.ecom.service;

import javax.transaction.Transactional;

public interface OrderService {
    Long checkOutOrder(String sessionId);
}
