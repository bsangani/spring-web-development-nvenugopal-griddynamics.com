package com.gd.ecom.mapper;

import com.gd.ecom.entity.CartItem;
import com.gd.ecom.entity.OrderItem;
import com.gd.ecom.model.CartItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
       CartItemDto toCartItemDto(CartItem cartItem);
       OrderItem toOrderItem(CartItem cartItem);
}
