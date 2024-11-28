package com.gd.ecom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    @NotNull(message = "Id should not be null")
    private Long id;

    @NotNull(message = "Quantity should not be null")
    @Min(value = 1,message = "Quantity should be least 1")
    private Integer quantity;
}
