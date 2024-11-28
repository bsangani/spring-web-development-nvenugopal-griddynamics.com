package com.gd.ecom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Title cannot be blank")
    @NotNull(message = "Title cannot be null")
    private String title;

    @PositiveOrZero(message = "Available quantity must be a positive number or zero")
    @NotNull(message = "Available quantity cannot be null")
    private Integer available;

    @DecimalMin(value = "0.00", message = "Price cannot be less than 0.00")
    @NotNull(message = "Price cannot be null")
    private BigDecimal price;
}
