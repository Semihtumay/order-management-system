package com.semihtumay.orderservice.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(

        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal totalPrice

) {
}
