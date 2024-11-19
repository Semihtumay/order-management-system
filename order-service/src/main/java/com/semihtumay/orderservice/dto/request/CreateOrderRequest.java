package com.semihtumay.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(

        @NotEmpty(message = "Product ID cannot be empty!")
        String productId,

        @NotNull(message = "Quantity cannot be null!")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {
}
