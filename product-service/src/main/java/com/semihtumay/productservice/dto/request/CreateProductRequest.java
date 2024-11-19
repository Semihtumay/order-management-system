package com.semihtumay.productservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotEmpty(message = "Name cannot be empty!")
        String name,

        @NotNull(message = "Price cannot be null!")
        @DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Quantity cannot be null!")
        @Min(value = 0, message = "Quantity must be at least 0")
        Integer quantity,

        @NotNull(message = "Tax cannot be null!")
        @DecimalMin(value = "0.0", message = "Tax must be greater than zero")
        BigDecimal tax,

        @DecimalMin(value = "0.0", message = "Discount must be greater than zero")
        BigDecimal discount
) {}
